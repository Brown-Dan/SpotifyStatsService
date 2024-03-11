package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Controller.Validator.StreamDataSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;
import java.util.function.Function;

import static uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamData.Builder.aRankedStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

@Service
public class StreamingDataService {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyRepository spotifyRepository;
    private final StreamDataSearchRequestValidator streamDataSearchRequestValidator;
    private final StreamingDataRepository streamingDataRepository;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public StreamingDataService(SpotifyAuthService spotifyAuthService, SpotifyRepository spotifyRepository, StreamDataSearchRequestValidator streamDataSearchRequestValidator, StreamingDataRepository streamingDataRepository) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyRepository = spotifyRepository;
        this.streamDataSearchRequestValidator = streamDataSearchRequestValidator;
        this.streamingDataRepository = streamingDataRepository;
    }

    public <T> Result<T, Error> getFromSpotify(String username, Function<SpotifyAuthData, Result<T, Error>> spotifyRequestGetter) {
        Result<SpotifyAuthData, Error> getSpotifyAuthDataResult = spotifyAuthService.getAuthData(username);

        if (getSpotifyAuthDataResult.isFailure()) {
            return new Result.Failure<>(getSpotifyAuthDataResult.getError());
        }
        return spotifyRequestGetter.apply(getSpotifyAuthDataResult.getValue());
    }

    public Result<StreamingData, Error> getRecentStreams(String username) {
        return getFromSpotify(username, spotifyRepository::getRecentStreamingData);
    }

    public Result<List<RankedStreamData>, Error> getTopStreams(String username) {
        Result<StreamingData, Error> apiResult = getFromSpotify(username, spotifyRepository::getTopTracks);
        if (apiResult.isFailure()) {
            return new Result.Failure<>(apiResult.getError());
        }
        return mapStreamingDataToRankedStreamData(apiResult.getValue(), username);
    }

    public Result<StreamingData, List<Error>> search(String username, StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Error> errors = streamDataSearchRequestValidator.validate(streamingDataSearchRequest);
        if (!errors.isEmpty()) {
            return new Result.Failure<>(errors);
        }
        return new Result.Success<>(streamingDataRepository.search(streamingDataSearchRequest, username));
    }

    private Result<List<RankedStreamData>, Error> mapStreamingDataToRankedStreamData(StreamingData streamingData, String username) {
        Result<StreamingData, Error> getStreamingDataResult = streamingDataRepository.getStreamingData(username);
        if (getStreamingDataResult.isFailure()) {
            return new Result.Failure<>(getStreamingDataResult.getError());
        }
        StreamingDataSearchRequest.Builder streamingDataSearchRequestBuilder = aStreamingDataSearchRequest()
                .withUsername(username)
                .withOrderBy(StreamDataSearchRequestOrderBy.STREAM_DATE_TIME.name())
                .withStart(getStreamingDataResult.getValue().firstStreamDateTime().toLocalDate())
                .withEnd(getStreamingDataResult.getValue().lastStreamDateTime().toLocalDate());

        return new Result.Success<>(streamingData.streamData().stream().map(streamData ->
                populateRankedStreamData(streamData, streamingDataSearchRequestBuilder, streamingData.streamData().indexOf(streamData))).toList());
    }

    private RankedStreamData populateRankedStreamData(StreamData streamData, StreamingDataSearchRequest.Builder searchRequestBuilder, int rank) {
        StreamingDataSearchRequest streamingDataSearchRequest = searchRequestBuilder.withUri(streamData.trackUri()).build();
        StreamingData streamingData = streamingDataRepository.search(streamingDataSearchRequest);

        long totalTimeStreamed = streamingData.streamData().stream().mapToLong(StreamData::timeStreamed).sum();
        return aRankedStreamData()
                .withTotalMsPlayed((int) totalTimeStreamed)
                .withLastStreamDateTime(streamingData.streamData().getLast().streamDateTime())
                .withTrackName(streamData.name())
                .withRanking(rank + 1)
                .withMinutesPlayed(((int) totalTimeStreamed / 1000) / 60)
                .withArtistName(streamData.artist())
                .withAlbumName(streamData.album())
                .withTrackUri(streamData.trackUri())
                .withTotalStreams(streamingData.streamCount())
                .build();
    }
}
