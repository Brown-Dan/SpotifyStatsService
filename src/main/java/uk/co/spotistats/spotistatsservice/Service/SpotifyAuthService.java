package uk.co.spotistats.spotistatsservice.Service;

import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyRefreshTokenResponse;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SpotifyResponseMapper;
import uk.co.spotistats.spotistatsservice.Repository.SpotifyAuthRepository;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.SpotifyRequestError;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.SpotifyClient;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static uk.co.spotistats.spotistatsservice.Controller.Model.Errors.fromSpotifyRequestError;

@Service
public class SpotifyAuthService {

    private final SpotifyResponseMapper spotifyResponseMapper;
    private final SpotifyAuthRepository spotifyAuthRepository;
    private final SpotifyClient spotifyClient;
    private final Algorithm algorithm;

    private static final String REDIRECT_URL = "https://spotifystats.co.uk/spotify/authenticate/callback";

    public SpotifyAuthService(SpotifyResponseMapper spotifyResponseMapper, SpotifyAuthRepository spotifyAuthRepository, SpotifyClient spotifyClient, Algorithm algorithm) {
        this.spotifyResponseMapper = spotifyResponseMapper;
        this.spotifyAuthRepository = spotifyAuthRepository;
        this.spotifyClient = spotifyClient;
        this.algorithm = algorithm;
    }

    public Result<SpotifyAuthData, Errors> insertSpotifyAuthData(SpotifyAuthData spotifyAuthData) {
        Optional<SpotifyAuthData> existingAuthData = spotifyAuthRepository.getAuthorizationDetailsByUsername(spotifyAuthData.userId());

        if (existingAuthData.isPresent()) {
            return new Result.Failure<>(Errors.fromError(Error.forbiddenToUpdate("authData", spotifyAuthData.userId())));
        }
        return new Result.Success<>(spotifyAuthRepository.insertSpotifyAuthData(spotifyAuthData));
    }

    public Result<SpotifyAuthData, Errors> getSpotifyAuthData(String userId) {
        Optional<SpotifyAuthData> existingAuthData = spotifyAuthRepository.getAuthorizationDetailsByUsername(userId);
        if (existingAuthData.isEmpty()) {
            return new Result.Failure<>(Errors.fromError(Error.notFound("spotifyAuthDetails", userId)));
        }
        SpotifyAuthData spotifyAuthData = existingAuthData.get();

        if (spotifyAuthData.hasValidAccessToken()) {
            return new Result.Success<>(spotifyAuthData);
        }
        return refreshToken(spotifyAuthData);
    }

    public boolean isAuthorized(String userId) {
        return spotifyAuthRepository.getAuthorizationDetailsByUsername(userId).isPresent();
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

    public Result<String, Errors> exchangeAccessToken(String accessToken) {
        Result<SpotifyAuthData, SpotifyRequestError> exchangeAccessTokenResult = spotifyClient.withAuthorization(System.getenv("SPOTIFY_BASE_64_AUTH"))
                .withContentType(ContentType.APPLICATION_FORM_URLENCODED)
                .exchangeAccessToken()
                .usingAccessToken(accessToken)
                .redirectTo(REDIRECT_URL)
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toSpotifyAuthData);

        if (exchangeAccessTokenResult.isFailure()) {
            return failure(Errors.fromSpotifyRequestError(exchangeAccessTokenResult.getError()));
        }
        Result<String, Errors> getUserIdResult = getUserId(exchangeAccessTokenResult.getValue());

        if(getUserIdResult.isFailure()){
            return failure(getUserIdResult.getError());
        }
        Result<SpotifyAuthData, Errors> insertAuthDataResult = insertSpotifyAuthData(exchangeAccessTokenResult.getValue().cloneBuilder().withUserId(getUserIdResult.getValue()).build());

        if (insertAuthDataResult.isFailure()){
            return failure(insertAuthDataResult.getError());
        }
        return success(getJwtToken(insertAuthDataResult.getValue().userId()));
    }

    private Result<String, Errors> getUserId(SpotifyAuthData spotifyAuthData) {
        Result<String, SpotifyRequestError> result = spotifyClient
                .withAccessToken(spotifyAuthData.accessToken())
                .getUserProfile()
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toUserProfile);

        return switch (result) {
            case Result.Failure(SpotifyRequestError error) -> failure(error);
            case Result.Success(String userId) -> success(userId);
        };
    }

    private Result<SpotifyAuthData, Errors> refreshToken(SpotifyAuthData spotifyAuthData) {
        Result<SpotifyRefreshTokenResponse, SpotifyRequestError> result = spotifyClient.withAuthorization(System.getenv("SPOTIFY_BASE_64_AUTH"))
                .withContentType(ContentType.APPLICATION_FORM_URLENCODED)
                .refreshToken()
                .usingRefreshToken(spotifyAuthData.refreshToken())
                .withClientId(System.getenv("SPOTIFY_CLIENT_ID"))
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toRefreshTokenResponse);

        return switch (result) {
            case Result.Failure(SpotifyRequestError error) ->
                    failure(Errors.fromSpotifyRequestError(spotifyAuthData.userId(), error));
            case Result.Success(SpotifyRefreshTokenResponse success) ->
                    success(spotifyAuthRepository.updateUserAuthData(spotifyAuthData.updateFromRefreshResponse(success)));
        };
    }

    private String getJwtToken(String username){
        return JWT.create()
                .withIssuer("spotiStatsService")
                .withSubject(username)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }


    private <T> Result<T, Errors> failure(SpotifyRequestError spotifyRequestError) {
        return new Result.Failure<>(fromSpotifyRequestError(spotifyRequestError));
    }

    private <T> Result<T, Errors> failure(Errors errors) {
        return new Result.Failure<>(errors);
    }

    private <T> Result<T, Errors> success(T success) {
        return new Result.Success<>(success);
    }
}
