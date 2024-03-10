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
import uk.co.spotistats.spotistatsservice.Domain.SpotifyRefreshTokenResponse;
import uk.co.spotistats.spotistatsservice.Domain.UserAuthData;
import uk.co.spotistats.spotistatsservice.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.UserAuthData.Builder.aUser;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Traverson traverson;
    private final ObjectMapper objectMapper;

    private static final String SPOTIFY_REFRESH_URL = "https://accounts.spotify.com/api/token";

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, Traverson traverson, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.traverson = traverson;
        this.objectMapper = objectMapper;
    }

    public Optional<Error> register(UserAuthData userAuthData) {
        Optional<UserAuthData> existingAuthData = userRepository.getAuthorizationDetailsByUsername(userAuthData.username());

        if (existingAuthData.isPresent()) {
            return Optional.of(Error.authorizationDetailsPresent(userAuthData.username()));
        }
        return Optional.empty();
    }

    public String getAccessToken(String username) {
        Optional<UserAuthData> existingAuthData = userRepository.getAuthorizationDetailsByUsername(username);
        if (existingAuthData.isEmpty()) {
            throw new NoSuchElementException("UserAuthData does not exist for username - %s".formatted(username));
        }
        UserAuthData userAuthData = existingAuthData.get();

        if (userAuthData.lastUpdated().isBefore(LocalDateTime.now().minusHours(1))) {
            return refreshToken(userAuthData);
        }
        return userAuthData.accessToken();
    }

    public boolean hasAuthData(String username){
        return userRepository.getAuthorizationDetailsByUsername(username).isPresent();
    }

    private UserAuthData updateUserAuthData(UserAuthData userAuthData) {
        return userRepository.updateUserAuthData(userAuthData);
    }

    private String refreshToken(UserAuthData userAuthData) {
        LOG.info("Refreshing access token for user - {}", userAuthData.username());
        Response<JSONObject> response = traverson.from(SPOTIFY_REFRESH_URL)
                .withHeader("content-type", "application/x-www-form-urlencoded")
                .withHeader("Authorization", "Basic MjAyNWI0OGQ5MjJhNDkwOTlkNjY1Y2JiZDI1NjM0MzY6NmNmZjZjMWRjM2MyNGZlY2FjNzU5ZThmZDY4ZTJkOWE=")
                .post(getRefreshTokenRequestBody(userAuthData.refreshToken()));

        SpotifyRefreshTokenResponse refreshResponse = objectMapper.convertValue(response.getResource(), SpotifyRefreshTokenResponse.class);
        return updateUserAuthData(buildUserAuthData(refreshResponse, userAuthData)).accessToken();
    }

    private TextBody getRefreshTokenRequestBody(String refreshToken) {
        String body = "grant_type=refresh_token&refresh_token=%s&client_id=2025b48d922a49099d665cbbd2563436".formatted(refreshToken);
        return new TextBody(body, "application/x-www-form-urlencoded");
    }

    private UserAuthData buildUserAuthData(SpotifyRefreshTokenResponse spotifyRefreshTokenResponse, UserAuthData originalUserAuthData) {
        return aUser()
                .withUsername(originalUserAuthData.username())
                .withAccessToken(spotifyRefreshTokenResponse.accessToken())
                .withRefreshToken(originalUserAuthData.refreshToken())
                .build();
    }
}
