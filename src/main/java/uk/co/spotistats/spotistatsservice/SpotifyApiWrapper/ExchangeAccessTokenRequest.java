package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import uk.co.autotrader.traverson.http.TextBody;

import java.util.HashMap;
import java.util.Map;

public class ExchangeAccessTokenRequest implements AbstractSpotifyPostRequest {

    private final Map<String, String> queryParams = new HashMap<>();
    private final SpotifyClient spotifyClient;
    private String accessToken;
    private String redirectUrl;


    private static final String URL = "https://accounts.spotify.com/api/token";

    ExchangeAccessTokenRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(Class<T> type) {
        return spotifyClient.fetch(this, type);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public ExchangeAccessTokenRequest usingAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public ExchangeAccessTokenRequest redirectTo(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    @Override
    public TextBody getBody() {
        String body = "grant_type=authorization_code&code=%s&redirect_uri=%s".formatted(accessToken, redirectUrl);
        return new TextBody(body, "application/x-www-form-urlencoded");
    }

    @Override
    public String getUrl() {
        return URL;
    }
}
