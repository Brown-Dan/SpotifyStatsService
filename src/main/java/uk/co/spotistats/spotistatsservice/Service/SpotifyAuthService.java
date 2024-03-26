package uk.co.spotistats.spotistatsservice.Service;

import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Model.User;
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
    private final JWTVerifier jwtVerifier;
    private final Algorithm algorithm;

    private static final String REDIRECT_URL = "https://spotify-stats-service-541cae0ce77e.herokuapp.com/authenticate/callback";

    private static final Logger LOG = LoggerFactory.getLogger(SpotifyAuthService.class);


    public SpotifyAuthService(SpotifyResponseMapper spotifyResponseMapper, SpotifyAuthRepository spotifyAuthRepository, SpotifyClient spotifyClient, JWTVerifier jwtVerifier, Algorithm algorithm) {
        this.spotifyResponseMapper = spotifyResponseMapper;
        this.spotifyAuthRepository = spotifyAuthRepository;
        this.spotifyClient = spotifyClient;
        this.jwtVerifier = jwtVerifier;
        this.algorithm = algorithm;
    }

    public SpotifyAuthData insertSpotifyAuthData(SpotifyAuthData spotifyAuthData) {
        return spotifyAuthRepository.getAuthorizationDetailsByUsername(spotifyAuthData.userId()).orElseGet(() ->
                spotifyAuthRepository.insertSpotifyAuthData(spotifyAuthData));
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
                    .setParameter("redirect_uri", REDIRECT_URL)
                    .setParameter("scope", "playlist-read-private user-follow-read user-top-read user-read-recently-played user-library-read user-read-private user-read-email playlist-modify-public playlist-modify-private")
                    .build().toURL().toString());
        } catch (URISyntaxException | MalformedURLException ignored) {
            throw new RuntimeException("Failure constructing URI");
        }
    }

    public Result<String, Errors> exchangeAccessToken(String accessToken) {
        LOG.info("Exchanging access token");
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
        Result<User, Errors> getUserIdResult = getUserProfile(exchangeAccessTokenResult.getValue());

        if (getUserIdResult.isFailure()) {
            return failure(getUserIdResult.getError());
        }
        SpotifyAuthData insertedSpotifyAuthData = insertSpotifyAuthData(exchangeAccessTokenResult.getValue().cloneBuilder().withUserId(getUserIdResult.getValue().id()).build());

        return success(getJwtToken(insertedSpotifyAuthData.userId()));
    }

    public Result<User, Errors> getUserProfile(SpotifyAuthData spotifyAuthData) {
        Result<User, SpotifyRequestError> result = spotifyClient
                .withAccessToken(spotifyAuthData.accessToken())
                .getUserProfile()
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toUserProfile);

        return switch (result) {
            case Result.Failure(SpotifyRequestError error) -> failure(error);
            case Result.Success(User userId) -> success(userId);
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

    public Result<String, Errors> refreshJwt(String jwt) {
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
            return success(getJwtToken(decodedJWT.getSubject()));
        } catch (JWTVerificationException jwtVerificationException){
            return failure(Errors.fromError(Error.jwtVerificationException(jwtVerificationException)));
        }
    }

    private String getJwtToken(String username) {
        return JWT.create()
                .withIssuer("SpotiStatsService")
                .withSubject(username)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(7200))
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
