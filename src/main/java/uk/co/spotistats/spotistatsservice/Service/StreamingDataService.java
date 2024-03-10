package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;

@Service
public class StreamingDataService {

    private final SpotifyAuthService spotifyAuthService;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);


    public StreamingDataService(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    public Result<String, Error> tokenTesting(String username) {
        Result<String, Error> getSpotifyAccessTokenResponse = spotifyAuthService.getAccessToken(username);

        if (getSpotifyAccessTokenResponse.isFailure()){
            return new Result.Failure<>(getSpotifyAccessTokenResponse.getError());
        }
        return getSpotifyAccessTokenResponse;
    }
}
