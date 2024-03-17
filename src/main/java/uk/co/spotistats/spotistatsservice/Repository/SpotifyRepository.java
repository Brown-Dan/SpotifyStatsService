package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.*;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.SimpleTopArtists;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.TopArtists;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracks;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SimpleTopArtistsToAdvancedTopArtistsMapper;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SpotifyResponseMapper;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.StreamingDataToRankedTracksMapper;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.SpotifyRequestError;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.SpotifyClient;

import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static uk.co.spotistats.spotistatsservice.Controller.Model.Errors.fromSpotifyRequestError;
import static uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue.LONG_TERM;
import static uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue.NOW;

@Repository
public class SpotifyRepository {

    private final SimpleTopArtistsToAdvancedTopArtistsMapper artistMapper;
    private final StreamingDataToRankedTracksMapper streamingDataToRankedTracksMapper;
    private final SpotifyResponseMapper spotifyResponseMapper;
    private final SpotifyClient spotifyClient;

    public SpotifyRepository(SimpleTopArtistsToAdvancedTopArtistsMapper artistMapper, StreamingDataToRankedTracksMapper streamingDataToRankedTracksMapper, SpotifyResponseMapper spotifyResponseMapper, SpotifyClient spotifyClient) {
        this.artistMapper = artistMapper;
        this.streamingDataToRankedTracksMapper = streamingDataToRankedTracksMapper;
        this.spotifyResponseMapper = spotifyResponseMapper;
        this.spotifyClient = spotifyClient;
    }

    public Result<RecentTracks, Errors> getRecentStreamingData(RecentTracksSearchRequest recentTracksSearchRequest) {
        Result<RecentTracks, SpotifyRequestError> result = spotifyClient
                .withAccessToken(recentTracksSearchRequest.authData().accessToken())
                .withContentType(APPLICATION_JSON)
                .getRecentStreamingData()
                .withBefore(NOW)
                .withLimit(recentTracksSearchRequest.limit())
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toRecentTracks);

        if (result.isFailure()) {
            return failure(recentTracksSearchRequest.userId(), result.getError());
        }
        if (recentTracksSearchRequest.createPlaylist()) {
            Result<Playlist, Errors> createPlaylistResult =
                    createPlaylist(CreatePlaylistRequest.fromRecentTracksSearchRequest(recentTracksSearchRequest, result.getValue().tracks()));
            if (!createPlaylistResult.isFailure()) {
                return success(result.getValue().addPlaylist(createPlaylistResult.getValue().id()));
            }
        }
        return success(result.getValue());
    }

    public Result<TopArtists, Errors> getTopArtists(TopArtistsSearchRequest searchRequest) {
        Result<SimpleTopArtists, SpotifyRequestError> result = spotifyClient
                        .withAccessToken(searchRequest.authData().accessToken())
                        .getTopArtists()
                        .withTimeRange(LONG_TERM)
                        .withLimit(searchRequest.limit())
                        .withOffset((searchRequest.page() - 1) * searchRequest.limit())
                        .fetchInto(JSONObject.class)
                        .map(jsonObject -> spotifyResponseMapper.fromTopArtists(jsonObject, searchRequest.page()));
        if (result.isFailure()) {
            return failure(searchRequest.userId(), result.getError());
        }
        return searchRequest.advanced() ? artistMapper.map(result.getValue(), searchRequest.userId()) : success(result.getValue());
    }

    public Result<TopTracks, Errors> getTopTracks(TopTracksSearchRequest searchRequest) {
        Result<StreamingData, SpotifyRequestError> result = spotifyClient
                .withAccessToken(searchRequest.authData().accessToken())
                .getTopTracks()
                .withTimeRange(LONG_TERM)
                .withLimit(searchRequest.limit())
                .withOffset((searchRequest.page() - 1) * searchRequest.limit())
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::fromTopTracks);

        if (result.isFailure()) {
            return failure(searchRequest.userId(), result.getError());
        }
        if (searchRequest.createPlaylist()) {
            Result<Playlist, Errors> createPlaylistResult = createPlaylist(CreatePlaylistRequest.fromTopTracksSearchRequest(searchRequest, result.getValue().streamData()));
            if (!createPlaylistResult.isFailure()) {
                return searchRequest.advanced() ? streamingDataToRankedTracksMapper.mapToAdvanced(result.getValue(), searchRequest, createPlaylistResult.getValue().id()) :
                        streamingDataToRankedTracksMapper.mapToSimple(result.getValue(), searchRequest, createPlaylistResult.getValue().id());
            }
        }
        return searchRequest.advanced() ? streamingDataToRankedTracksMapper.mapToAdvanced(result.getValue(), searchRequest, null) :
                streamingDataToRankedTracksMapper.mapToSimple(result.getValue(), searchRequest, null);
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

    private <T> Result<T, Errors> failure(Errors errors) {
        return new Result.Failure<>(errors);
    }

    private <T> Result<T, Errors> success(T success) {
        return new Result.Success<>(success);
    }
}
