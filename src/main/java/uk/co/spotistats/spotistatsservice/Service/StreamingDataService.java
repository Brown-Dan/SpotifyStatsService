package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyRepository;

@Service
public class StreamingDataService {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyRepository spotifyRepository;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public StreamingDataService(SpotifyAuthService spotifyAuthService, SpotifyRepository spotifyRepository) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyRepository = spotifyRepository;
    }

    public Result<StreamingData, Error> getRecentStreams(String username) {
        Result<SpotifyAuthData, Error> getSpotifyAuthDataResult = spotifyAuthService.getAuthData(username);

        if (getSpotifyAuthDataResult.isFailure()) {
            return new Result.Failure<>(getSpotifyAuthDataResult.getError());
        }
        return spotifyRepository.getRecentStreamingData(getSpotifyAuthDataResult.getValue());
    }
}
