package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import com.alibaba.fastjson2.JSON;
import org.apache.hc.core5.http.ContentType;
import org.springframework.stereotype.Component;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.TraversonBuilder;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.Header;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParam;

import java.util.HashMap;
import java.util.Map;


@Component
public class SpotifyClient {

    private final Traverson traverson;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();

    private static final String RECENT_STREAMS_URL = "https://api.spotify.com/v1/me/player/recently-played";
    private static final String TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks";
    private static final String USERS_URL = "https://api.spotify.com/v1/users/";
    private static final String ADD_TRACKS = "https://api.spotify.com/v1/playlists/%s/tracks";

    public SpotifyClient(Traverson traverson) {
        this.traverson = traverson;
    }

    public SpotifyClient withAccessToken(String accessToken) {
        headers.put("Authorization", "Bearer %s ".formatted(accessToken));
        return this;
    }

    public SpotifyClient withContentType(ContentType contentType) {
        headers.put("content-type", contentType.getMimeType());
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

    public CreatePlaylistRequest createPlaylist() {
        return new CreatePlaylistRequest(this);
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
        queryParams.put("time_range", topTracksRequest.getTimeRange());

        TraversonBuilder traversonBuilder = traverson.from(TOP_TRACKS_URL);
        addHeaders(traversonBuilder);
        addQueryParams(traversonBuilder);

        return new SpotifyResponseWrapper<>(traversonBuilder.get(type));
    }

    <T> SpotifyResponseWrapper<T> fetch(CreatePlaylistRequest createPlaylistRequest, Class<T> type) {
        TraversonBuilder traversonBuilder = traverson.from(USERS_URL + createPlaylistRequest.getUserId() + "/playlists");
        addHeaders(traversonBuilder);
        addQueryParams(traversonBuilder);

        return new SpotifyResponseWrapper<>(traversonBuilder.post(createPlaylistRequest.getBody(), type));
    }

    <T> SpotifyResponseWrapper<T> fetch(AddTracksRequest addTracksRequest, Class<T> type) {
        TraversonBuilder traversonBuilder = traverson.from(ADD_TRACKS.formatted(addTracksRequest.getPlaylistId()));
        addHeaders(traversonBuilder);
        addQueryParams(traversonBuilder);

        return new SpotifyResponseWrapper<>(traversonBuilder.post(addTracksRequest.getBody(), type));
    }

    <T> SpotifyResponseWrapper<T> fetch(CreatePlaylistRequest createPlaylistRequest, AddTracksRequest addTracksRequest, Class<T> type) {
        SpotifyResponseWrapper<T> spotifyResponseWrapper = fetch(createPlaylistRequest, type);

        if (spotifyResponseWrapper.isFailure()) {
            return spotifyResponseWrapper;
        }
        String playlistId = JSON.parseObject(spotifyResponseWrapper.getResponse().getResource().toString()).getString("id");
        addTracksRequest.withPlaylistId(playlistId);

        return fetch(addTracksRequest.withPlaylistId(playlistId), type);
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
