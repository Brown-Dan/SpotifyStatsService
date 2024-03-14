package uk.co.spotistats.spotistatsservice.Service;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.Response;
import uk.co.autotrader.traverson.http.TextBody;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyRefreshTokenResponse;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyAuthRepository;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
public class SpotifyAuthService {

    private final SpotifyAuthRepository spotifyAuthRepository;
    private final Traverson traverson;
    private final ObjectMapper objectMapper;

    private static final String SPOTIFY_REFRESH_URL = "https://accounts.spotify.com/api/token";
    private static final String SPOTIFY_PROFILE_DATA_URL = "https://api.spotify.com/v1/me";

    private static final Logger LOG = LoggerFactory.getLogger(SpotifyAuthService.class);

    public SpotifyAuthService(SpotifyAuthRepository spotifyAuthRepository, Traverson traverson, ObjectMapper objectMapper) {
        this.spotifyAuthRepository = spotifyAuthRepository;
        this.traverson = traverson;
        this.objectMapper = objectMapper;
    }

    public Result<SpotifyAuthData, Errors> insertSpotifyAuthData(SpotifyAuthData spotifyAuthData) {
        Optional<SpotifyAuthData> existingAuthData = spotifyAuthRepository.getAuthorizationDetailsByUsername(spotifyAuthData.userId());

        if (existingAuthData.isPresent()) {
            return new Result.Failure<>(Errors.fromError(Error.forbiddenToUpdate("authData", spotifyAuthData.userId())));
        }
        return new Result.Success<>(spotifyAuthRepository.insertSpotifyAuthData(spotifyAuthData));
    }

    public Result<SpotifyAuthData, Error> getSpotifyAuthData(String username) {
        Optional<SpotifyAuthData> existingAuthData = spotifyAuthRepository.getAuthorizationDetailsByUsername(username);
        if (existingAuthData.isEmpty()) {
            return new Result.Failure<>(Error.notFound("spotifyAuthDetails", username));
        }
        SpotifyAuthData spotifyAuthData = existingAuthData.get();

        if (spotifyAuthData.hasValidAccessToken()) {
            return new Result.Success<>(spotifyAuthData);
        }
        return refreshToken(spotifyAuthData);
    }

    public RedirectView redirect() {
        try {
            return new RedirectView(new URIBuilder()
                    .setScheme("https")
                    .setHost("accounts.spotify.com")
                    .setPath("/authorize")
                    .setParameter("client_id", System.getenv("SPOTIFY_CLIENT_ID"))
                    .setParameter("response_type", "code")
                    .setParameter("redirect_uri", "https://spotifystats.co.uk/spotify/authenticate/callback")
                    .setParameter("scope", "playlist-read-private user-follow-read user-top-read user-read-recently-played user-library-read user-read-private user-read-email playlist-modify-public playlist-modify-private")
                    .build().toURL().toString());
        } catch (URISyntaxException | MalformedURLException ignored) {
            throw new RuntimeException("Failure constructing URI");
        }
    }

    public Result<SpotifyAuthData, Errors> exchangeAccessToken(String accessToken) {
        Response<JSONObject> response = traverson.from(SPOTIFY_REFRESH_URL)
                .withHeader("content-type", "application/x-www-form-urlencoded")
                .withHeader("Authorization", System.getenv("SPOTIFY_BASE_64_AUTH"))
                .post(buildAccessTokenExchangeBody(accessToken));

        SpotifyAuthData spotifyAuthData = objectMapper.convertValue(response.getResource(), SpotifyAuthData.class);
        return insertSpotifyAuthData(spotifyAuthData.cloneBuilder().withUserId(getUserId(spotifyAuthData)).build());
    }

    private String getUserId(SpotifyAuthData spotifyAuthData) {
        Response<JSONObject> response = traverson.from(SPOTIFY_PROFILE_DATA_URL)
                .withHeader("Authorization", "Bearer %s".formatted(spotifyAuthData.accessToken()))
                .get();
        return response.getResource().get("id").toString();
    }

    private Result<SpotifyAuthData, Error> refreshToken(SpotifyAuthData spotifyAuthData) {
        Response<JSONObject> response = traverson.from(SPOTIFY_REFRESH_URL)
                .withHeader("content-type", "application/x-www-form-urlencoded")
                .withHeader("Authorization", System.getenv("SPOTIFY_BASE_64_AUTH"))
                .post(buildRefreshTokenRequestBody(spotifyAuthData.refreshToken()));
        if (!response.isSuccessful()) {
            LOG.info("Failed to refresh access token for user - {} received response - {}", spotifyAuthData.userId(), response.getStatusCode());
            return new Result.Failure<>(Error.failedToRefreshAccessToken(spotifyAuthData.userId(), response.getStatusCode()));
        }
        LOG.info("Refreshed access token for user - {}", spotifyAuthData.userId());
        SpotifyRefreshTokenResponse spotifyRefreshTokenResponse = objectMapper.convertValue(response.getResource(), SpotifyRefreshTokenResponse.class);
        return new Result.Success<>(spotifyAuthRepository.updateUserAuthData
                (spotifyAuthData.updateFromRefreshResponse(spotifyRefreshTokenResponse)));
    }

    private TextBody buildRefreshTokenRequestBody(String refreshToken) {
        String body = "grant_type=refresh_token&refresh_token=%s&client_id=%s".formatted(refreshToken, System.getenv("SPOTIFY_CLIENT_ID"));
        return new TextBody(body, "application/x-www-form-urlencoded");
    }

    private TextBody buildAccessTokenExchangeBody(String accessToken) {
        String body = "grant_type=authorization_code&code=%s&redirect_uri=https://spotifystats.co.uk/spotify/authenticate/callback".formatted(accessToken);
        return new TextBody(body, "application/x-www-form-urlencoded");
    }
}
