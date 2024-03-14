package uk.co.spotistats.spotistatsservice.SpotifyClientApi;

import org.apache.hc.core5.http.ContentType;
import org.springframework.stereotype.Component;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.TraversonBuilder;
import uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum.Header;
import uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum.QueryParam;

import java.util.HashMap;
import java.util.Map;


@Component
public class SpotifyClient {

    private Traverson traverson;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();

    private static final String RECENT_STREAMS_URL = "https://api.spotify.com/v1/me/player/recently-played";
    private static final String TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks";

    public SpotifyClient() {
    }

    public SpotifyClient(Traverson traverson) {
        this.traverson = traverson;
    }

    public SpotifyClient withAccessToken(String accessToken) {
        headers.put("Authorization", "Bearer %s ".formatted(accessToken));
        return this;
    }

    public SpotifyClient withContentType(ContentType contentType) {
        headers.put("content-type", contentType.toString());
        return this;
    }

    public <T> SpotifyClient withQueryParam(QueryParam param, T value) {
        queryParams.put(param.getValue(), String.valueOf(value));
        return this;
    }

    public <T> SpotifyClient withHeader(Header header, T value) {
        queryParams.put(header.getValue(), String.valueOf(value));
        return this;
    }

    public RecentStreamingDataRequest getRecentStreamingData() {
        return new RecentStreamingDataRequest(this);
    }
    public TopTracksRequest getTopTracks() {
        return new TopTracksRequest(this);
    }

    <T> SpotifyResponseWrapper<T> fetch(RecentStreamingDataRequest recentStreamingDataRequest, Class<T> type) {
        queryParams.put("limit", recentStreamingDataRequest.getLimit());
        queryParams.put("before", recentStreamingDataRequest.getBefore());

        TraversonBuilder traversonBuilder = traverson.from(RECENT_STREAMS_URL);
        addHeaders(traversonBuilder);
        addQueryParams(traversonBuilder);

        return new SpotifyResponseWrapper<>(traversonBuilder.get(type));
    }

    <T> SpotifyResponseWrapper<T> fetch(TopTracksRequest topTracksRequest, Class<T> type) {
        queryParams.put("limit", topTracksRequest.getLimit());
        queryParams.put("before", topTracksRequest.getTimeRange());

        TraversonBuilder traversonBuilder = traverson.from(TOP_TRACKS_URL);
        addHeaders(traversonBuilder);
        addQueryParams(traversonBuilder);

        return new SpotifyResponseWrapper<>(traversonBuilder.get(type));
    }

    void addHeaders(TraversonBuilder traversonBuilder) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            traversonBuilder.withHeader(header.getKey(), header.getValue());
        }
    }

    void addQueryParams(TraversonBuilder traversonBuilder) {
        for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
            traversonBuilder.withQueryParam(queryParam.getKey(), queryParam.getValue());
        }
    }
}
