package uk.co.spotistats.spotistatsservice.Service;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.Response;
import uk.co.autotrader.traverson.http.TextBody;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyRefreshTokenResponse;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyAuthRepository;

import java.util.Optional;

@Service
public class SpotifyAuthService {

    private final SpotifyAuthRepository spotifyAuthRepository;
    private final Traverson traverson;
    private final ObjectMapper objectMapper;

    private static final String SPOTIFY_REFRESH_URL = "https://accounts.spotify.com/api/token";

    private static final Logger LOG = LoggerFactory.getLogger(SpotifyAuthService.class);

    public SpotifyAuthService(SpotifyAuthRepository spotifyAuthRepository, Traverson traverson, ObjectMapper objectMapper) {
        this.spotifyAuthRepository = spotifyAuthRepository;
        this.traverson = traverson;
        this.objectMapper = objectMapper;
    }

    public Optional<Error> insertSpotifyAuthData(SpotifyAuthData spotifyAuthData) {
        Optional<SpotifyAuthData> existingAuthData = spotifyAuthRepository.getAuthorizationDetailsByUsername(spotifyAuthData.username());

        if (existingAuthData.isPresent()) {
            return Optional.of(Error.authorizationDetailsPresent(spotifyAuthData.username()));
        }
        spotifyAuthRepository.insertSpotifyAuthData(spotifyAuthData);
        return Optional.empty();
    }

    public Result<String, Error> getAccessToken(String username) {
        Optional<SpotifyAuthData> existingAuthData = spotifyAuthRepository.getAuthorizationDetailsByUsername(username);
        if (existingAuthData.isEmpty()) {
            return new Result.Failure<>(Error.authorizationDetailsNotPresent(username));
        }
        SpotifyAuthData spotifyAuthData = existingAuthData.get();

        if (spotifyAuthData.hasValidAccessToken()) {
            return new Result.Success<>(spotifyAuthData.accessToken());
        }
        return refreshToken(spotifyAuthData);
    }

    private Result<String, Error> refreshToken(SpotifyAuthData spotifyAuthData) {
        Response<JSONObject> response = traverson.from(SPOTIFY_REFRESH_URL)
                .withHeader("content-type", "application/x-www-form-urlencoded")
                .withHeader("Authorization", "Basic MjAyNWI0OGQ5MjJhNDkwOTlkNjY1Y2JiZDI1NjM0MzY6NmNmZjZjMWRjM2MyNGZlY2FjNzU5ZThmZDY4ZTJkOWE=")
                .post(buildRefreshTokenRequestBody(spotifyAuthData.refreshToken()));
        if (!response.isSuccessful()) {
            LOG.info("Failed to refresh access token for user - {} received response - {}", spotifyAuthData.username(), response.getStatusCode());
            return new Result.Failure<>(Error.failedToRefreshAccessToken(spotifyAuthData.username(), response.getStatusCode()));
        }
        LOG.info("Refreshed access token for user - {}", spotifyAuthData.username());
        SpotifyRefreshTokenResponse spotifyRefreshTokenResponse = objectMapper.convertValue(response.getResource(), SpotifyRefreshTokenResponse.class);
        return new Result.Success<>(spotifyAuthRepository.updateUserAuthData
                (spotifyAuthData.updateFromRefreshResponse(spotifyRefreshTokenResponse)).accessToken());
    }

    private TextBody buildRefreshTokenRequestBody(String refreshToken) {
        String body = "grant_type=refresh_token&refresh_token=%s&client_id=2025b48d922a49099d665cbbd2563436".formatted(refreshToken);
        return new TextBody(body, "application/x-www-form-urlencoded");
    }
}
