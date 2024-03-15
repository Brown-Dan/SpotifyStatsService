package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import com.alibaba.fastjson2.JSON;
import org.apache.hc.core5.http.ContentType;
import org.springframework.stereotype.Component;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.TraversonBuilder;
import uk.co.autotrader.traverson.http.Response;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.Header;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParam;

import java.util.HashMap;
import java.util.Map;

@Component
public class SpotifyClient {

    private final Traverson traverson;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();

    public SpotifyClient(Traverson traverson) {
        this.traverson = traverson;
    }

    public SpotifyClient withAccessToken(String accessToken) {
        headers.put("Authorization", "Bearer %s ".formatted(accessToken));
        return this;
    }

    public SpotifyClient withAuthorization(String value) {
        headers.put("Authorization", value);
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

    public UserProfileRequest getUserProfile() {
        return new UserProfileRequest(this);
    }

    public RecentStreamingDataRequest getRecentStreamingData() {
        return new RecentStreamingDataRequest(this);
    }

    public TopTracksGetRequest getTopTracks() {
        return new TopTracksGetRequest(this);
    }

    public CreatePlaylistRequest createPlaylist() {
        return new CreatePlaylistRequest(this);
    }

    public RefreshTokenRequest refreshToken() {
        return new RefreshTokenRequest(this);
    }

    public ExchangeAccessTokenRequest exchangeAccessToken() {
        return new ExchangeAccessTokenRequest(this);
    }

    <T> SpotifyResponseWrapper<T> fetch(AbstractSpotifyGetRequest abstractSpotifyGetRequest, Class<T> clazz) {
        queryParams.putAll(abstractSpotifyGetRequest.getQueryParams());

        TraversonBuilder traversonBuilder = fromUrl(abstractSpotifyGetRequest.getUrl());

        return wrap(traversonBuilder.get(clazz));
    }

    <T> SpotifyResponseWrapper<T> fetch(AbstractSpotifyPostRequest abstractSpotifyPostRequest, Class<T> clazz) {
        TraversonBuilder traversonBuilder = fromUrl(abstractSpotifyPostRequest.getUrl());
        return wrap(traversonBuilder.post(abstractSpotifyPostRequest.getBody(), clazz));
    }

    <T> SpotifyResponseWrapper<T> fetch(CreatePlaylistRequest createPlaylistRequest, AddTracksRequest addTracksRequest, Class<T> clazz) {
        SpotifyResponseWrapper<T> spotifyResponseWrapper = fetch(createPlaylistRequest, clazz);

        if (spotifyResponseWrapper.isFailure()) {
            return spotifyResponseWrapper;
        }
        String playlistId = JSON.parseObject(spotifyResponseWrapper.getResponse().getResource().toString()).getString("id");
        addTracksRequest.withPlaylistId(playlistId);
        fetch(addTracksRequest.withPlaylistId(playlistId), clazz);

        return spotifyResponseWrapper;
    }

    TraversonBuilder fromUrl(String url) {
        TraversonBuilder traversonBuilder = traverson.from(url);
        addHeaders(traversonBuilder);
        addQueryParams(traversonBuilder);
        return traversonBuilder;
    }

    <T> SpotifyResponseWrapper<T> wrap(Response<T> response) {
        return new SpotifyResponseWrapper<>(response);
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
