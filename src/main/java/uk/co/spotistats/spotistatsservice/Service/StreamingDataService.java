package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.*;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.ErrorKey;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponseTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.AdvancedTopArtist;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.TopArtists;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracks;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataUploadRepository;
import uk.co.spotistats.spotistatsservice.Service.Validator.RecentTracksSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Service.Validator.StreamDataSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Service.Validator.TopArtistsSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Service.Validator.TopTracksSearchRequestValidator;

import java.util.List;

import static uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest.Builder.aRecentTracksSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy.DATE_ASC;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.AdvancedTopArtist.Builder.anAdvancedTopArtist;

@Service
@EnableAsync
public class StreamingDataService {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyRepository spotifyRepository;
    private final StreamingDataRepository streamingDataRepository;
    private final StreamingDataUploadRepository streamingDataUploadRepository;
    private final StreamDataSearchRequestValidator streamDataSearchRequestValidator;
    private final TopTracksSearchRequestValidator topTracksSearchRequestValidator;
    private final RecentTracksSearchRequestValidator recentTracksSearchRequestValidator;
    private final StreamingDataUploadService streamingDataUploadService;
    private final TopArtistsSearchRequestValidator topArtistsSearchRequestValidator;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public StreamingDataService(SpotifyAuthService spotifyAuthService, SpotifyRepository spotifyRepository, StreamingDataRepository streamingDataRepository, StreamingDataUploadRepository streamingDataUploadRepository, StreamDataSearchRequestValidator streamDataSearchRequestValidator, TopTracksSearchRequestValidator topTracksSearchRequestValidator, RecentTracksSearchRequestValidator recentTracksSearchRequestValidator, StreamingDataUploadService streamingDataUploadService, TopArtistsSearchRequestValidator topArtistsSearchRequestValidator) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyRepository = spotifyRepository;
        this.streamingDataRepository = streamingDataRepository;
        this.streamingDataUploadRepository = streamingDataUploadRepository;
        this.streamDataSearchRequestValidator = streamDataSearchRequestValidator;
        this.topTracksSearchRequestValidator = topTracksSearchRequestValidator;
        this.recentTracksSearchRequestValidator = recentTracksSearchRequestValidator;
        this.streamingDataUploadService = streamingDataUploadService;
        this.topArtistsSearchRequestValidator = topArtistsSearchRequestValidator;
    }

    public Result<RecentTracks, Errors> getRecentTracks(RecentTracksSearchRequest searchRequest) {
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

    public Result<TopTracks, Errors> getTopTracks(TopTracksSearchRequest searchRequest) {
        Errors validationErrors = topTracksSearchRequestValidator.validate(searchRequest);
        if (validationErrors.hasErrors()) {
            return failure(validationErrors);
        }

        Result<SpotifyAuthData, Errors> spotifyAuthDataResult = spotifyAuthService.getSpotifyAuthData(searchRequest.userId());
        if (spotifyAuthDataResult.isFailure()) {
            return failure(spotifyAuthDataResult.getError());
        }
        return spotifyRepository.getTopTracks(searchRequest.cloneBuilder().withAuthData(spotifyAuthDataResult.getValue()).build());
    }

    public Result<TopArtists, Errors> getTopArtists(TopArtistsSearchRequest searchRequest) {
        Errors validationErrors = topArtistsSearchRequestValidator.validate(searchRequest);
        if (validationErrors.hasErrors()) {
            return failure(validationErrors);
        }

        Result<SpotifyAuthData, Errors> spotifyAuthDataResult = spotifyAuthService.getSpotifyAuthData(searchRequest.userId());
        if (spotifyAuthDataResult.isFailure()) {
            return failure(spotifyAuthDataResult.getError());
        }
        return spotifyRepository.getTopArtists(searchRequest.cloneBuilder().withAuthData(spotifyAuthDataResult.getValue()).build());
    }

    public Result<AdvancedTopArtist, Errors> getArtist(ArtistSearchRequest searchRequest) {
        Result<StreamingData, Error> streamingData = streamingDataRepository.getStreamingData(searchRequest.userId());

        if (streamingData.isFailure()) {
            return failure(Errors.fromError(new Error(null, "uploaded streaming data is required to get advanced artist data", ErrorKey.STREAMING_DATA_NOT_UPLOADED)));
        }

        StreamingDataSearchRequest streamingDataSearchRequest = aStreamingDataSearchRequest()
                .withOrderBy(DATE_ASC.toString())
                .withArtist(searchRequest.artistName())
                .withStartDate(streamingData.getValue().firstStreamDateTime().toLocalDate())
                .withEndDate(streamingData.getValue().lastStreamDateTime().toLocalDate())
                .withUserId(searchRequest.userId()).build();

        SearchResponse searchResponse= streamingDataRepository.search(streamingDataSearchRequest);

        long totalMsStreamed = searchResponse.tracks().stream().mapToLong(SearchResponseTrack::totalMsPlayed).sum();

        return success(anAdvancedTopArtist()
                .withName(searchRequest.artistName())
                .withFirstStreamedDate(searchResponse.tracks().isEmpty() ? null : searchResponse.tracks().getFirst().streamDateTime())
                .withLastStreamedDate(searchResponse.tracks().isEmpty() ? null : searchResponse.tracks().getLast().streamDateTime())
                .withTotalMsStreamed(totalMsStreamed)
                .withTotalStreams(searchResponse.tracks().size())
                .withTotalMinutesStreamed(((int) totalMsStreamed / 1000) / 60)
                .build());
    }

    public Result<AdvancedTrack, Errors> getByTrackUri(TrackUriSearchRequest trackUriSearchRequest) {
        if (!streamingDataUploadService.hasStreamingData(trackUriSearchRequest.username())) {
            return failure(Errors.fromError(new Error(null, "uploaded streaming data is required to use search insights", ErrorKey.STREAMING_DATA_NOT_UPLOADED)));
        }

        StreamingDataSearchRequest streamingDataSearchRequest = aStreamingDataSearchRequest()
                .withUri(trackUriSearchRequest.trackUri())
                .withOrderBy(DATE_ASC.toString())
                .withUserId(trackUriSearchRequest.username()).build();

        SearchResponse streamingData = streamingDataRepository.search(streamingDataSearchRequest);

        return success(AdvancedTrack.fromSearchResponse(streamingData));
    }

    public Result<SearchResponse, Errors> search(StreamingDataSearchRequest searchRequest) {
        Errors errors = streamDataSearchRequestValidator.validate(searchRequest);
        if (errors.hasErrors()) {
            return failure(errors);
        }
        Result<SearchResponse, Errors> result = success(streamingDataRepository.search(searchRequest));

        if (result.isFailure()) {
            return failure(errors);
        }
        if (searchRequest.createPlaylist()) {
            Result<Playlist, Errors> createPlaylistResult = spotifyRepository.createPlaylist(CreatePlaylistRequest.fromStreamingDataSearchRequest(searchRequest, result.getValue().tracks(),
                    spotifyAuthService.getSpotifyAuthData(searchRequest.userId()).getValue()));
            if (createPlaylistResult.isFailure()) {
                return failure(createPlaylistResult.getError());
            }
            return success(result.getValue().cloneBuilder().withCreatedPlaylist(true).withPlaylistId(createPlaylistResult.getValue().id()).build());
        }
        return success(result.getValue());
    }

    @Async
    public void syncRecentStreamData(StreamingData streamingData) {
        LOG.info("Syncing streaming data for user - {}", streamingData.username());
        RecentTracksSearchRequest recentTracksSearchRequest = aRecentTracksSearchRequest().withCreatePlaylist(false).withUserId(streamingData.username()).withLimit(50).build();
        Result<RecentTracks, Errors> recentTracksResult = getRecentTracks(recentTracksSearchRequest);
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
