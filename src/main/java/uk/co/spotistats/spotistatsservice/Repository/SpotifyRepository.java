package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.TextBody;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.CreatePlaylistRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SpotifyResponseJsonToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum.QueryParamValue;
import uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum.SpotifyRequestError;
import uk.co.spotistats.spotistatsservice.SpotifyClientApi.SpotifyClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static uk.co.spotistats.spotistatsservice.Controller.Model.Errors.fromSpotifyRequestError;
import static uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum.QueryParamValue.NOW;

@Repository
public class SpotifyRepository {

    private final Traverson traverson;
    private final SpotifyResponseJsonToStreamingDataMapper spotifyResponseMapper;
    private final ObjectMapper objectMapper;
    private final SpotifyClient spotifyClient;

    private static final String USERS_URL = "https://api.spotify.com/v1/users/";
    private static final String PLAYLISTS_URL = "https://api.spotify.com/v1/playlists/";

    public SpotifyRepository(Traverson traverson, SpotifyResponseJsonToStreamingDataMapper spotifyResponseMapper, ObjectMapper objectMapper, SpotifyClient spotifyClient) {
        this.traverson = traverson;
        this.spotifyResponseMapper = spotifyResponseMapper;
        this.objectMapper = objectMapper;
        this.spotifyClient = spotifyClient;
    }

    public Result<StreamingData, Errors> getRecentStreamingData(SpotifySearchRequest spotifySearchRequest) {
        Result<StreamingData, SpotifyRequestError> result = spotifyClient
                .withAccessToken(spotifySearchRequest.authData().accessToken())
                .withContentType(APPLICATION_JSON)
                .getRecentStreamingData()
                .withBefore(NOW)
                .withLimit(spotifySearchRequest.limit())
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::fromRecentStreams);

        if (result.isFailure()) {
            return failure(spotifySearchRequest.userId(), result.getError());
        }
        return new Result.Success<>(result.getValue());
    }

    public Result<StreamingData, Errors> getTopTracks(SpotifySearchRequest spotifySearchRequest) {
        Result<StreamingData, SpotifyRequestError> result = spotifyClient
                .withAccessToken(spotifySearchRequest.authData().accessToken())
                .getTopTracks()
                .withTimeRange(QueryParamValue.LONG_TERM)
                .withLimit(spotifySearchRequest.limit())
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::fromTopTracks);

        if (result.isFailure()) {
            return failure(spotifySearchRequest.userId(), result.getError());
        }
        return new Result.Success<>(result.getValue());
    }

//    public Result<Playlist, Errors> createPlaylist(CreatePlaylistRequest createPlaylistRequest) {
//        Response<JSONObject> playlistCreationResponse = traverson.from(USERS_URL + createPlaylistRequest.authData().userId() + "/playlists")
//                .withHeader("Authorization", createPlaylistRequest.authData().getHeader())
//                .withHeader("Content-Type", "application/json")
//                .post(buildPlaylistCreationRequestBody(createPlaylistRequest));
//
//        if (!playlistCreationResponse.isSuccessful()) {
//            return failure(createPlaylistRequest.authData().userId(), playlistCreationResponse.getStatusCode());
//        }
//        Playlist playlist = spotifyResponseMapper.toPlaylist(playlistCreationResponse.getResource());
//
//        Response<JSONObject> addTracksResponse = traverson.from(PLAYLISTS_URL + playlist.id() + "/tracks")
//                .withHeader("Authorization", createPlaylistRequest.authData().getHeader())
//                .withHeader("Content-Type", "application/json")
//                .post(buildPlaylistAddTracksRequestBody(createPlaylistRequest));
//
//        if (!addTracksResponse.isSuccessful()) {
//            return failure(createPlaylistRequest.authData().userId(), addTracksResponse.getStatusCode());
//        }
//        return new Result.Success<>(playlist.cloneBuilder().withTracks(createPlaylistRequest.trackUris()).build());
//    }

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

    private <T> Result<T, Errors> failure(String userId, SpotifyRequestError spotifyRequestError) {
        return new Result.Failure<>(fromSpotifyRequestError(userId, spotifyRequestError));
    }
}
