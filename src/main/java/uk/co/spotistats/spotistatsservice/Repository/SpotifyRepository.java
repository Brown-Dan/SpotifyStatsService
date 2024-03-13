package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.Response;
import uk.co.autotrader.traverson.http.TextBody;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.CreatePlaylistRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Playlist;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SpotifyResponseJsonToStreamingDataMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SpotifyRepository {

    private final Traverson traverson;
    private final SpotifyResponseJsonToStreamingDataMapper spotifyResponseJsonToStreamingDataMapper;
    private final ObjectMapper objectMapper;

    private static final String RECENT_STREAMS_URL = "https://api.spotify.com/v1/me/player/recently-played";
    private static final String TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks";
    private static final String USERS_URL = "https://api.spotify.com/v1/users/";
    private static final String PLAYLISTS_URL = "https://api.spotify.com/v1/playlists/";

    public SpotifyRepository(Traverson traverson, SpotifyResponseJsonToStreamingDataMapper spotifyResponseJsonToStreamingDataMapper, ObjectMapper objectMapper) {
        this.traverson = traverson;
        this.spotifyResponseJsonToStreamingDataMapper = spotifyResponseJsonToStreamingDataMapper;
        this.objectMapper = objectMapper;
    }

    public Result<StreamingData, Errors> getRecentStreamingData(SpotifySearchRequest spotifySearchRequest) {
        Response<String> response = traverson.from(RECENT_STREAMS_URL)
                .withHeader("Authorization", "Bearer %s ".formatted(spotifySearchRequest.authData().accessToken()))
                .withQueryParam("limit", spotifySearchRequest.limit().toString())
                .withQueryParam("before", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString())
                .get(String.class);
        if (response.isSuccessful()) {
            return spotifyResponseJsonToStreamingDataMapper.mapFromRecentStreamsJson(JSONObject.parseObject(response.getResource(), JSONObject.class));
        }
        return new Result.Failure<>(Errors.fromError(responseToError(response, spotifySearchRequest.authData().userId())));
    }

    public Result<StreamingData, Errors> getTopTracks(SpotifySearchRequest spotifySearchRequest) {
        Response<JSONObject> response = traverson.from(TOP_TRACKS_URL)
                .withHeader("Authorization", "Bearer %s ".formatted(spotifySearchRequest.authData().accessToken()))
                .withQueryParam("limit", spotifySearchRequest.limit().toString())
                .withQueryParam("time_range", "long_term")
                .get();
        if (response.isSuccessful()) {
            return spotifyResponseJsonToStreamingDataMapper.mapFromTopStreamsJson(response.getResource());
        }
        return new Result.Failure<>(Errors.fromError(responseToError(response, spotifySearchRequest.authData().userId())));
    }

    public Result<Playlist, Error> createPlaylist(CreatePlaylistRequest createPlaylistRequest) {
        Response<JSONObject> playlistCreationResponse = traverson.from(USERS_URL + createPlaylistRequest.authData().userId() + "/playlists")
                .withHeader("Authorization", "Bearer %s ".formatted(createPlaylistRequest.authData().accessToken()))
                .withHeader("Content-Type", "application/json")
                .post(buildPlaylistCreationRequestBody(createPlaylistRequest));

        if (!playlistCreationResponse.isSuccessful()) {
            return new Result.Failure<>(responseToError(playlistCreationResponse, createPlaylistRequest.authData().userId()));
        }
        Playlist playlist = spotifyResponseJsonToStreamingDataMapper.mapFromPlaylistJson(playlistCreationResponse.getResource());

        Response<JSONObject> addTracksResponse = traverson.from(PLAYLISTS_URL + playlist.id() + "/tracks")
                .withHeader("Authorization", "Bearer %s ".formatted(createPlaylistRequest.authData().accessToken()))
                .withHeader("Content-Type", "application/json")
                .post(buildPlaylistAddTracksRequestBody(createPlaylistRequest));

        if (!addTracksResponse.isSuccessful()) {
            return new Result.Failure<>(responseToError(addTracksResponse, createPlaylistRequest.authData().userId()));
        }

        return new Result.Success<>(playlist.cloneBuilder().withTracks(createPlaylistRequest.trackUris()).build());
    }


    private TextBody buildPlaylistCreationRequestBody(CreatePlaylistRequest createPlaylistRequest) {
        Map<String, String> body = new HashMap<>();
        body.put("name", createPlaylistRequest.name());
        body.put("description", createPlaylistRequest.description());
        try {
            return new TextBody(objectMapper.writeValueAsString(body), "application/json");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private TextBody buildPlaylistAddTracksRequestBody(CreatePlaylistRequest createPlaylistRequest) {
        Map<String, List<String>> body = new HashMap<>();
        body.put("uris", createPlaylistRequest.trackUris());
        try {
            return new TextBody(objectMapper.writeValueAsString(body), "application/json");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Error responseToError(Response<?> response, String username) {
        return switch (response.getStatusCode()) {
            case 401 -> Error.notFound("spotifyAuthDetails", username);
            case 403 -> Error.userNotRegisteredDev(username);
            case 429 -> Error.spotifyRateLimitExceeded();
            default -> Error.unknownError("spotify.api", "Failed to get streamingData");
        };
    }
}
