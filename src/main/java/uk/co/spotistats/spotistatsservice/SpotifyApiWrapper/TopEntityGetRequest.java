package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue;

import java.util.HashMap;
import java.util.Map;

public class TopEntityGetRequest implements AbstractSpotifyGetRequest {
    private final Map<String, String> queryParams = new HashMap<>();
    private final SpotifyClient spotifyClient;

    private final String url;

    TopEntityGetRequest(SpotifyClient spotifyClient, String url) {
        this.url = url;
        this.spotifyClient = spotifyClient;
    }

    public <T> TopEntityGetRequest withLimit(T limit) {
        queryParams.put("limit", String.valueOf(limit));
        return this;
    }

    public TopEntityGetRequest withTimeRange(QueryParamValue timeRange) {
        queryParams.put("time_range", timeRange.getValue());
        return this;
    }

    public <T> TopEntityGetRequest withOffset(T offset) {
        queryParams.put("offset", String.valueOf(offset));
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
        return url;
    }
}
