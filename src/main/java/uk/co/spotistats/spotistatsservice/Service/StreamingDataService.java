package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TrackUriSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracksResource;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataUploadRepository;
import uk.co.spotistats.spotistatsservice.Service.Mapper.StreamingDataToRankedTracksMapper;
import uk.co.spotistats.spotistatsservice.Service.Validator.RecentTracksSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Service.Validator.StreamDataSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Service.Validator.TopTracksSearchRequestValidator;

import java.util.List;

import static uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest.Builder.aRecentTracksSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

@Service
@EnableAsync
public class StreamingDataService {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyRepository spotifyRepository;
    private final StreamingDataRepository streamingDataRepository;
    private final StreamingDataUploadRepository streamingDataUploadRepository;
    private final StreamingDataToRankedTracksMapper streamingDataToRankedTracksMapper;
    private final StreamDataSearchRequestValidator streamDataSearchRequestValidator;
    private final TopTracksSearchRequestValidator topTracksSearchRequestValidator;
    private final RecentTracksSearchRequestValidator recentTracksSearchRequestValidator;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public StreamingDataService(SpotifyAuthService spotifyAuthService, SpotifyRepository spotifyRepository, StreamDataSearchRequestValidator streamDataSearchRequestValidator, StreamingDataRepository streamingDataRepository, StreamingDataUploadRepository streamingDataUploadRepository, StreamingDataToRankedTracksMapper streamingDataToRankedTracksMapper, TopTracksSearchRequestValidator topTracksSearchRequestValidator, RecentTracksSearchRequestValidator recentTracksSearchRequestValidator) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyRepository = spotifyRepository;
        this.streamDataSearchRequestValidator = streamDataSearchRequestValidator;
        this.streamingDataRepository = streamingDataRepository;
        this.streamingDataUploadRepository = streamingDataUploadRepository;
        this.streamingDataToRankedTracksMapper = streamingDataToRankedTracksMapper;
        this.topTracksSearchRequestValidator = topTracksSearchRequestValidator;
        this.recentTracksSearchRequestValidator = recentTracksSearchRequestValidator;
    }

    public Result<RecentTracks, Errors> getRecentStreams(RecentTracksSearchRequest searchRequest) {
        Errors validationErrors = recentTracksSearchRequestValidator.validate(searchRequest);
        if (validationErrors.hasErrors()) {
            return failure(validationErrors);
        }

        Result<SpotifyAuthData, Errors> spotifyAuthDataResult = spotifyAuthService.getSpotifyAuthData(searchRequest.userId());
        if (spotifyAuthDataResult.isFailure()) {
            return failure(spotifyAuthDataResult.getError());
        }

        return spotifyRepository.getRecentStreamingData(searchRequest.cloneBuilder().withAuthData(spotifyAuthDataResult.getValue()).build());
    }

    public Result<TopTracksResource, Errors> getTopTracks(TopTracksSearchRequest searchRequest) {
        Errors validationErrors = topTracksSearchRequestValidator.validate(searchRequest);
        if (validationErrors.hasErrors()) {
            return failure(validationErrors);
        }

        Result<SpotifyAuthData, Errors> spotifyAuthDataResult = spotifyAuthService.getSpotifyAuthData(searchRequest.userId());
        if (spotifyAuthDataResult.isFailure()) {
            return failure(spotifyAuthDataResult.getError());
        }

        Result<StreamingData, Errors> result = spotifyRepository
                .getTopTracks(searchRequest.cloneBuilder().withAuthData(spotifyAuthDataResult.getValue()).build());
        return switch (result) {
            case Result.Failure(Errors errors) -> failure(errors);
            case Result.Success(StreamingData streamingData) ->
                    searchRequest.advanced() ? streamingDataToRankedTracksMapper.mapToAdvanced(streamingData, searchRequest) :
                            streamingDataToRankedTracksMapper.mapToSimple(streamingData, searchRequest);
        };
    }

    public Result<AdvancedTrack, Errors> getByTrackUri(TrackUriSearchRequest trackUriSearchRequest) {
        StreamingDataSearchRequest streamingDataSearchRequest = aStreamingDataSearchRequest()
                .withUri(trackUriSearchRequest.trackUri())
                .withUsername(trackUriSearchRequest.username()).build();

        StreamingData streamingData = streamingDataRepository.search(streamingDataSearchRequest);

        return success(AdvancedTrack.fromStreamingData(streamingData));
    }


    public Result<StreamingData, Errors> search(StreamingDataSearchRequest searchRequest) {
        Errors errors = streamDataSearchRequestValidator.validate(searchRequest);
        if (errors.hasErrors()) {
            failure(errors);
        }
        return success(streamingDataRepository.search(searchRequest));
    }

    @Async
    public void syncRecentStreamData(StreamingData streamingData) {
        LOG.info("Syncing streaming data for user - {}", streamingData.username());
        RecentTracksSearchRequest recentTracksSearchRequest = aRecentTracksSearchRequest().withCreatePlaylist(false).withUserId(streamingData.username()).withLimit(50).build();
        Result<RecentTracks, Errors> recentTracksResult = getRecentStreams(recentTracksSearchRequest);
        if (recentTracksResult.isFailure()) {
            LOG.error("Failure syncing streaming data for user - {}", streamingData.username());
            return;
        }
        List<StreamData> filteredStreamData = recentTracksResult.getValue().tracks().stream().map(StreamData::fromRecentTrack).filter(streamData -> streamData.streamDateTime().isAfter(streamingData.lastStreamDateTime())).toList();
        streamingDataUploadRepository.updateStreamingData(streamingData.updateStreamingDataFromSync(recentTracksResult.getValue()).cloneBuilder().withSize(filteredStreamData.size() + streamingData.size()).build(), streamingData.username());
        filteredStreamData.forEach(streamData -> streamingDataUploadRepository.insertStreamData(streamData, streamingData.username()));
    }

    private <T> Result<T, Errors> failure(Errors errors) {
        return new Result.Failure<>(errors);
    }

    private <T> Result<T, Errors> success(T success) {
        return new Result.Success<>(success);
    }
}
