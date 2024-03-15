package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import uk.co.autotrader.traverson.http.TextBody;

import java.util.HashMap;
import java.util.Map;

public class RefreshTokenRequest implements AbstractSpotifyPostRequest {

    private final Map<String, String> queryParams = new HashMap<>();
    private final SpotifyClient spotifyClient;
    private String refreshToken;
    private String clientId;

    private static final String URL = "https://accounts.spotify.com/api/token";

    RefreshTokenRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(Class<T> type) {
        return spotifyClient.fetch(this, type);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public RefreshTokenRequest usingRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public RefreshTokenRequest withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public TextBody getBody() {
        String body = "grant_type=refresh_token&refresh_token=%s&client_id=%s".formatted(refreshToken, clientId);
        return new TextBody(body, "application/x-www-form-urlencoded");
    }

    @Override
    public String getUrl() {
        return URL;
    }
}
