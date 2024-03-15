package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import com.alibaba.fastjson2.JSON;
import org.apache.hc.core5.http.ContentType;
import uk.co.autotrader.traverson.http.TextBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTracksRequest implements AbstractSpotifyPostRequest {
    List<String> trackUris;
    String playlistId;
    SpotifyClient spotifyClient;
    CreatePlaylistRequest createPlaylistRequest;

    private static final String URL = "https://api.spotify.com/v1/playlists/%s/tracks";

    AddTracksRequest(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public AddTracksRequest(List<String> trackUris, CreatePlaylistRequest createPlaylistRequest) {
        this.trackUris = trackUris;
        this.createPlaylistRequest = createPlaylistRequest;
    }

    public AddTracksRequest withTrackUris(List<String> trackUris) {
        this.trackUris = trackUris;
        return this;
    }

    public <T> AddTracksRequest withPlaylistId(T playlistId){
        this.playlistId = String.valueOf(playlistId);
        return this;
    }

    public <T> SpotifyResponseWrapper<T> fetchInto(Class<T> type) {
        if (spotifyClient == null) {
            return createPlaylistRequest.fetchInto(this, type);
        }
        return spotifyClient.fetch(this, type);
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public TextBody getBody() {
        Map<String, List<String>> body = new HashMap<>();
        body.put("uris", trackUris);
        return new TextBody(JSON.toJSONString(body), ContentType.APPLICATION_JSON.getMimeType());
    }

    @Override
    public String getUrl() {
        return URL.formatted(playlistId);
    }
}
