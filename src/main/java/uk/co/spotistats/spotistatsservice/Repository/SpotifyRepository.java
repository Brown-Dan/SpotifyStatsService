package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.CreatePlaylistRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Playlist;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SpotifyResponseMapper;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.SpotifyRequestError;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.SpotifyClient;

import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static uk.co.spotistats.spotistatsservice.Controller.Model.Errors.fromSpotifyRequestError;
import static uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue.LONG_TERM;
import static uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue.NOW;

@Repository
public class SpotifyRepository {

    private final SpotifyResponseMapper spotifyResponseMapper;
    private final SpotifyClient spotifyClient;

    public SpotifyRepository(SpotifyResponseMapper spotifyResponseMapper, SpotifyClient spotifyClient) {
        this.spotifyResponseMapper = spotifyResponseMapper;
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
                .map(spotifyResponseMapper::toStreamingData);

        if (result.isFailure()) {
            return failure(spotifySearchRequest.userId(), result.getError());
        }
        if (spotifySearchRequest.createPlaylist()) {
            createPlaylist(CreatePlaylistRequest.fromSpotifySearchRequest(spotifySearchRequest, result.getValue().streamData()));
        }
        return success(result.getValue());
    }

    public Result<StreamingData, Errors> getTopTracks(TopTracksSearchRequest searchRequest) {
        Result<StreamingData, SpotifyRequestError> result = spotifyClient
                .withAccessToken(searchRequest.authData().accessToken())
                .getTopTracks()
                .withTimeRange(LONG_TERM)
                .withLimit(searchRequest.limit())
                .withOffset((searchRequest.page() -  1) * searchRequest.limit())
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toRankedStreamingData);

        if (result.isFailure()) {
            return failure(searchRequest.userId(), result.getError());
        }
        if (searchRequest.createPlaylist()) {
            createPlaylist(CreatePlaylistRequest.fromTopStreamsSearchRequest(searchRequest, result.getValue().streamData()));
        }
        return success(result.getValue());
    }

    public Result<Playlist, Errors> createPlaylist(CreatePlaylistRequest createPlaylistRequest) {
        Result<Playlist, SpotifyRequestError> result = spotifyClient
                .withAccessToken(createPlaylistRequest.authData().accessToken())
                .withContentType(APPLICATION_JSON)
                .createPlaylist()
                .withName(createPlaylistRequest.name())
                .withDescription(createPlaylistRequest.description())
                .withUserId(createPlaylistRequest.authData().userId())
                .withTracks(createPlaylistRequest.trackUris())
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toPlaylist);

        if (result.isFailure()) {
            return failure(createPlaylistRequest.authData().userId(), result.getError());
        }
        return success(result.getValue().cloneBuilder().withTracks(createPlaylistRequest.trackUris()).build());
    }

    private <T> Result<T, Errors> failure(String userId, SpotifyRequestError spotifyRequestError) {
        return new Result.Failure<>(fromSpotifyRequestError(userId, spotifyRequestError));
    }

    private <T> Result<T, Errors> success(T success) {
        return new Result.Success<>(success);
    }
}
