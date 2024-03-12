package uk.co.spotistats.spotistatsservice.Service;

import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Controller.Validator.StreamDataSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamData.Builder.aRankedStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamingData.Builder.aRankedStreamingData;
import static uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

@Service
public class StreamingDataService {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyRepository spotifyRepository;
    private final StreamDataSearchRequestValidator streamDataSearchRequestValidator;
    private final StreamingDataRepository streamingDataRepository;

    public StreamingDataService(SpotifyAuthService spotifyAuthService, SpotifyRepository spotifyRepository, StreamDataSearchRequestValidator streamDataSearchRequestValidator, StreamingDataRepository streamingDataRepository) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyRepository = spotifyRepository;
        this.streamDataSearchRequestValidator = streamDataSearchRequestValidator;
        this.streamingDataRepository = streamingDataRepository;
    }

    public <T> Result<T, Errors> getFromSpotify(SpotifySearchRequest spotifySearchRequest, Function<SpotifySearchRequest, Result<T, Errors>> spotifyRepositoryGetter) {
        Result<SpotifyAuthData, Error> getSpotifyAuthDataResult = spotifyAuthService.getSpotifyAuthData(spotifySearchRequest.username());

        if (getSpotifyAuthDataResult.isFailure()) {
            return new Result.Failure<>(Errors.fromError(getSpotifyAuthDataResult.getError()));
        }
        return spotifyRepositoryGetter.apply(spotifySearchRequest.cloneBuilder()
                .withAuthData(getSpotifyAuthDataResult.getValue())
                .build());
    }

    public Result<StreamingData, Errors> getRecentStreams(SpotifySearchRequest spotifySearchRequest) {
        return getFromSpotify(spotifySearchRequest, spotifyRepository::getRecentStreamingData);
    }

    public Result<RankedStreamingData, Errors> getTopStreams(SpotifySearchRequest spotifySearchRequest) {
        Result<StreamingData, Errors> apiResult = getFromSpotify(spotifySearchRequest, spotifyRepository::getTopTracks);
        if (apiResult.isFailure()) {
            return new Result.Failure<>(apiResult.getError());
        }
        return mapStreamingDataToRankedStreamData(apiResult.getValue(), spotifySearchRequest.username());
    }

    public Result<StreamingData, Errors> search(StreamingDataSearchRequest streamingDataSearchRequest) {
        Errors errors = streamDataSearchRequestValidator.validate(streamingDataSearchRequest);
        if (errors.hasErrors()) {
            return new Result.Failure<>(errors);
        }
        return new Result.Success<>(streamingDataRepository.search(streamingDataSearchRequest));
    }

    private Result<RankedStreamingData, Errors> mapStreamingDataToRankedStreamData(StreamingData streamingData, String username) {
        Result<StreamingData, Error> getStreamingDataResult = streamingDataRepository.getStreamingData(username);
        if (getStreamingDataResult.isFailure()) {
            return new Result.Failure<>(Errors.fromError(getStreamingDataResult.getError()));
        }
        StreamingDataSearchRequest.Builder streamingDataSearchRequestBuilder = aStreamingDataSearchRequest()
                .withUsername(username)
                .withOrderBy(StreamDataSearchRequestOrderBy.STREAM_DATE_TIME.name())
                .withStart(getStreamingDataResult.getValue().firstStreamDateTime().toLocalDate())
                .withEnd(getStreamingDataResult.getValue().lastStreamDateTime().toLocalDate());

        List<RankedStreamData> rankedStreamData = streamingData.streamData().stream().map(streamData ->
                populateRankedStreamData(streamData, streamingDataSearchRequestBuilder,
                        streamingData.streamData().indexOf(streamData))).toList();

        return new Result.Success<>(aRankedStreamingData()
                .withRankedStreamData(rankedStreamData)
                .withSize(rankedStreamData.size())
                .build());
    }

    private RankedStreamData populateRankedStreamData(StreamData streamData, StreamingDataSearchRequest.Builder searchRequestBuilder, int rank) {
        StreamingDataSearchRequest streamingDataSearchRequest = searchRequestBuilder.withUri(streamData.trackUri()).build();
        StreamingData streamingData = streamingDataRepository.search(streamingDataSearchRequest);

        long totalTimeStreamed = streamingData.streamData().stream().mapToLong(StreamData::timeStreamed).sum();
        RankedStreamData.Builder rankedStreamData = aRankedStreamData()
                .withTotalMsPlayed((int) totalTimeStreamed)
                .withTrackName(streamData.name())
                .withRanking(rank + 1)
                .withMinutesPlayed(((int) totalTimeStreamed / 1000) / 60)
                .withArtistName(streamData.artist())
                .withAlbumName(streamData.album())
                .withTrackUri(streamData.trackUri())
                .withTotalStreams(streamingData.size());

        return streamingData.streamData().isEmpty() ? rankedStreamData.build() :
                rankedStreamData.withLastStreamDateTime(Optional.ofNullable(streamingData.streamData().getLast())
                        .map(StreamData::streamDateTime).orElse(null)).build();
    }
}
