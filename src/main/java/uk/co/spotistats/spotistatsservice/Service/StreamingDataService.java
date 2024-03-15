package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Service.Validator.StreamDataSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.TopTracksResource;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataUploadRepository;
import uk.co.spotistats.spotistatsservice.Service.Mapper.StreamingDataToTopTracksMapper;
import uk.co.spotistats.spotistatsservice.Service.Validator.TopTracksSearchRequestValidator;

import java.util.List;
import java.util.function.Function;

import static uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest.Builder.aSpotifySearchRequest;

@Service
@EnableAsync
public class StreamingDataService {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyRepository spotifyRepository;
    private final StreamDataSearchRequestValidator streamDataSearchRequestValidator;
    private final StreamingDataRepository streamingDataRepository;
    private final StreamingDataUploadRepository streamingDataUploadRepository;
    private final StreamingDataToTopTracksMapper streamingDataToTopTracksMapper;
    private final TopTracksSearchRequestValidator topTracksSearchRequestValidator;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public StreamingDataService(SpotifyAuthService spotifyAuthService, SpotifyRepository spotifyRepository, StreamDataSearchRequestValidator streamDataSearchRequestValidator, StreamingDataRepository streamingDataRepository, StreamingDataUploadRepository streamingDataUploadRepository, StreamingDataToTopTracksMapper streamingDataToTopTracksMapper, TopTracksSearchRequestValidator topTracksSearchRequestValidator) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyRepository = spotifyRepository;
        this.streamDataSearchRequestValidator = streamDataSearchRequestValidator;
        this.streamingDataRepository = streamingDataRepository;
        this.streamingDataUploadRepository = streamingDataUploadRepository;
        this.streamingDataToTopTracksMapper = streamingDataToTopTracksMapper;
        this.topTracksSearchRequestValidator = topTracksSearchRequestValidator;
    }

    public <T> Result<T, Errors> getFromSpotify(SpotifySearchRequest spotifySearchRequest, Function<SpotifySearchRequest, Result<T, Errors>> spotifyRepositoryGetter) {
        Result<SpotifyAuthData, Errors> result = spotifyAuthService.getSpotifyAuthData(spotifySearchRequest.userId());

        if (result.isFailure()) {
            failure(result.getError());
        }
        return spotifyRepositoryGetter.apply(spotifySearchRequest.cloneBuilder()
                .withAuthData(result.getValue())
                .build());
    }

    public Result<StreamingData, Errors> getRecentStreams(SpotifySearchRequest spotifySearchRequest) {
        return getFromSpotify(spotifySearchRequest, spotifyRepository::getRecentStreamingData);
    }

    public Result<TopTracksResource, Errors> getTopTracks(TopTracksSearchRequest searchRequest) {
        Errors validationErrors = topTracksSearchRequestValidator.validate(searchRequest);
        if (validationErrors.hasErrors()){
            return failure(validationErrors);
        }

        Result<SpotifyAuthData, Errors> getSpotifyAuthDataResult = spotifyAuthService.getSpotifyAuthData(searchRequest.userId());
        if (getSpotifyAuthDataResult.isFailure()) {
            return failure(getSpotifyAuthDataResult.getError());
        }

        Result<StreamingData, Errors> result = spotifyRepository
                .getTopTracks(searchRequest.cloneBuilder().withAuthData(getSpotifyAuthDataResult.getValue()).build());
        return switch (result) {
            case Result.Failure(Errors errors) -> failure(errors);
            case Result.Success(StreamingData streamingData) ->
                    searchRequest.ranked() ? streamingDataToTopTracksMapper.map(streamingData, searchRequest.userId(), searchRequest.page()) :
                            streamingDataToTopTracksMapper.map(streamingData, searchRequest.page());
        };
    }

    public Result<StreamingData, Errors> search(StreamingDataSearchRequest streamingDataSearchRequest) {
        Errors errors = streamDataSearchRequestValidator.validate(streamingDataSearchRequest);
        if (errors.hasErrors()) {
            failure(errors);
        }
        return success(streamingDataRepository.search(streamingDataSearchRequest));
    }

    @Async
    public void syncRecentStreamData(StreamingData streamingData) {
        LOG.info("Syncing streaming data for user - {}", streamingData.username());
        SpotifySearchRequest spotifySearchRequest = aSpotifySearchRequest().withUserId(streamingData.username()).withLimit(50).build();
        Result<StreamingData, Errors> streamingDataResult = getRecentStreams(spotifySearchRequest);
        if (streamingDataResult.isFailure()) {
            LOG.error("Failure syncing streaming data for user - {}", streamingData.username());
            return;
        }
        List<StreamData> filteredStreamData = streamingDataResult.getValue().streamData().stream().filter(streamData -> streamData.streamDateTime().isAfter(streamingData.lastStreamDateTime())).toList();
        streamingDataUploadRepository.updateStreamingData(streamingData.updateStreamingDataFromSync(streamingDataResult.getValue()).cloneBuilder().withSize(filteredStreamData.size() + streamingData.size()).build(), streamingData.username());
        filteredStreamData.forEach(streamData -> streamingDataUploadRepository.insertStreamData(streamData, streamingData.username()));
    }

    private <T> Result<T, Errors> failure(Errors errors) {
        return new Result.Failure<>(errors);
    }

    private <T> Result<T, Errors> success(T success) {
        return new Result.Success<>(success);
    }
}
