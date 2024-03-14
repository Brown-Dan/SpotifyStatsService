package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import com.alibaba.fastjson2.JSON;
import org.apache.hc.core5.http.ContentType;
import uk.co.autotrader.traverson.http.TextBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePlaylistRequest {
    String name;
    String description;
    String userId;
    private final SpotifyClient spotifyClient;

    CreatePlaylistRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public <T> CreatePlaylistRequest withName(T name) {
        this.name = String.valueOf(name);
        return this;
    }

    public <T> CreatePlaylistRequest withUserId(T userId) {
        this.userId = String.valueOf(userId);
        return this;
    }

    public <T> CreatePlaylistRequest withDescription(T description) {
        this.description = String.valueOf(description);
        return this;
    }

    public AddTracksRequest withTracks(List<String> trackUris) {
        return new AddTracksRequest(trackUris, this);
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(Class<T> type) {
        return spotifyClient.fetch(this, type);
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(AddTracksRequest addTracksRequest, Class<T> type) {
        return spotifyClient.fetch(this, addTracksRequest, type);
    }

    public String getUserId() {
        return userId;
    }

    TextBody getBody() {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description);
        return new TextBody(JSON.toJSONString(body), ContentType.APPLICATION_JSON.toString());
    }
}
