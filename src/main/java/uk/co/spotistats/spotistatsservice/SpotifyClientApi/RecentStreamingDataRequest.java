package uk.co.spotistats.spotistatsservice.SpotifyClientApi;

import uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum.QueryParamValue;

public class RecentStreamingDataRequest {
    private String limit;
    private String before;
    private final SpotifyClient spotifyClient;

    RecentStreamingDataRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public <T> RecentStreamingDataRequest withLimit(T limit) {
        this.limit = String.valueOf(limit);
        return this;
    }

    public <T> RecentStreamingDataRequest withBefore(QueryParamValue before) {
        this.before = before.getValue();
        return this;
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(Class<T> type) {
        return spotifyClient.fetch(this, type);
    }

    String getLimit() {
        return limit;
    }

    String getBefore() {
        return before;
    }
}
