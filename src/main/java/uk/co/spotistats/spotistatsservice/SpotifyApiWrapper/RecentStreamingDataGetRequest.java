package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue;

import java.util.HashMap;
import java.util.Map;

public class RecentStreamingDataGetRequest implements AbstractSpotifyGetRequest {
    Map<String, String> queryParams = new HashMap<>();
    private final SpotifyClient spotifyClient;
    private static final String URL = "https://api.spotify.com/v1/me/player/recently-played";

    RecentStreamingDataGetRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public <T> RecentStreamingDataGetRequest withLimit(T limit) {
        queryParams.put("limit", String.valueOf(limit));
        return this;
    }

    public RecentStreamingDataGetRequest withBefore(QueryParamValue before) {
        queryParams.put("before", before.getValue());
        return this;
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
