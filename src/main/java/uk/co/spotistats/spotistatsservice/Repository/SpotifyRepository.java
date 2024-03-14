package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.CreatePlaylistRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Playlist;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SpotifyResponseJsonToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.SpotifyRequestError;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.SpotifyClient;

import java.time.LocalDateTime;

import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static uk.co.spotistats.spotistatsservice.Controller.Model.Errors.fromSpotifyRequestError;
import static uk.co.spotistats.spotistatsservice.Domain.Request.CreatePlaylistRequest.Builder.aCreatePlaylistRequest;
import static uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.QueryParamValue.NOW;

@Repository
public class SpotifyRepository {

    private final SpotifyResponseJsonToStreamingDataMapper spotifyResponseMapper;
    private final SpotifyClient spotifyClient;

    private static final Logger LOG = LoggerFactory.getLogger(SpotifyRepository.class);

    public SpotifyRepository(SpotifyResponseJsonToStreamingDataMapper spotifyResponseMapper, SpotifyClient spotifyClient) {
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
                .map(spotifyResponseMapper::fromRecentStreams);

        if (result.isFailure()) {
            return failure(spotifySearchRequest.userId(), result.getError());
        }
        if (spotifySearchRequest.createPlaylist()){
            LOG.info("Creating playlist from recent songs");
            createPlaylist(aCreatePlaylistRequest()
                    .withName(spotifySearchRequest.userId() + " : %s".formatted(LocalDateTime.now()))
                    .withDescription("Autogenerated playlist for the most recent %s songs".formatted(spotifySearchRequest.limit()))
                    .withTrackUris(result.getValue().streamData().stream().map(StreamData::trackUri).toList())
                    .withSpotifyAuthData(spotifySearchRequest.authData())
                    .build());
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

    public Result<Playlist, Errors> createPlaylist(CreatePlaylistRequest createPlaylistRequest) {
        Result<Playlist, SpotifyRequestError> result = spotifyClient
                .withAccessToken(createPlaylistRequest.authData().accessToken())
                .withContentType(APPLICATION_JSON)
                .createPlaylist()
                .withName(createPlaylistRequest.name())
                .withDescription(createPlaylistRequest.description())
                .withTracks(createPlaylistRequest.trackUris())
                .fetchInto(JSONObject.class)
                .map(spotifyResponseMapper::toPlaylist);

        if (result.isFailure()) {
            return failure(createPlaylistRequest.authData().userId(), result.getError());
        }
        return new Result.Success<>(result.getValue().cloneBuilder().withTracks(createPlaylistRequest.trackUris()).build());
    }

    private <T> Result<T, Errors> failure(String userId, SpotifyRequestError spotifyRequestError) {
        return new Result.Failure<>(fromSpotifyRequestError(userId, spotifyRequestError));
    }
}
