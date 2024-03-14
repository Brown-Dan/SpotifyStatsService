package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue;

public class TopTracksRequest {
    String limit;
    String timeRange;
    private final SpotifyClient spotifyClient;

    TopTracksRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public <T> TopTracksRequest withLimit(T limit) {
        this.limit = String.valueOf(limit);
        return this;
    }

    public TopTracksRequest withTimeRange(QueryParamValue timeRange) {
        this.timeRange = timeRange.getValue();
        return this;
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(Class<T> type) {
        return spotifyClient.fetch(this, type);
    }

    String getLimit() {
        return limit;
    }

    String getTimeRange() {
        return timeRange;
    }
}
