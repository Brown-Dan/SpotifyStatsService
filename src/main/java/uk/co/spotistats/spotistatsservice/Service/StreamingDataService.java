package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Controller.Validator.StreamDataSearchRequestValidator;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyRepository;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;
import java.util.function.Function;

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

    public Result<StreamingData, Error> getRecentStreams(String username) {
        return sendApiRequest(username, spotifyRepository::getRecentStreamingData);
    }

    public Result<StreamingData, Error> getTopStreams(String username) {
        return sendApiRequest(username, spotifyRepository::getTopTracks);
    }

    public Result<StreamingData, Error> sendApiRequest(String username, Function<SpotifyAuthData, Result<StreamingData, Error>> get) {
        Result<SpotifyAuthData, Error> getSpotifyAuthDataResult = spotifyAuthService.getAuthData(username);

        if (getSpotifyAuthDataResult.isFailure()) {
            return new Result.Failure<>(getSpotifyAuthDataResult.getError());
        }
        return get.apply(getSpotifyAuthDataResult.getValue());
    }

    public Result<StreamingData, List<Error>> get(String username, StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Error> errors = streamDataSearchRequestValidator.validate(streamingDataSearchRequest);
        if (!errors.isEmpty()) {
            return new Result.Failure<>(errors);
        }
        return new Result.Success<>(streamingDataRepository.get(streamingDataSearchRequest, username));
    }
}
