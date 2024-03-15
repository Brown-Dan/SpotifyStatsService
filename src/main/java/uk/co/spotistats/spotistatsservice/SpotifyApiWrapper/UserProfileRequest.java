package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import java.util.HashMap;
import java.util.Map;

public class UserProfileRequest implements AbstractSpotifyGetRequest {
    private final Map<String, String> queryParams = new HashMap<>();
    private final SpotifyClient spotifyClient;
    private static final String URL = "https://api.spotify.com/v1/me";

    UserProfileRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(Class<T> type) {
        return spotifyClient.fetch(this, type);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public String getUrl() {
        return URL;
    }
}
