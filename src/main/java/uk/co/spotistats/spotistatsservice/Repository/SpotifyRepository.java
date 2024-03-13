package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Repository;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.Response;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.SpotifyResponseJsonToStreamingDataMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
public class SpotifyRepository {

    private final Traverson traverson;
    private final SpotifyResponseJsonToStreamingDataMapper spotifyResponseJsonToStreamingDataMapper;

    private static final String RECENT_STREAMS_URL = "https://api.spotify.com/v1/me/player/recently-played";
    private static final String TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks";

    public SpotifyRepository(Traverson traverson, SpotifyResponseJsonToStreamingDataMapper spotifyResponseJsonToStreamingDataMapper) {
        this.traverson = traverson;
        this.spotifyResponseJsonToStreamingDataMapper = spotifyResponseJsonToStreamingDataMapper;
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

    private Error responseToError(Response<?> response, String username) {
        return switch (response.getStatusCode()) {
            case 401 -> Error.notFound("spotifyAuthDetails", username);
            case 403 -> Error.userNotRegisteredDev(username);
            case 429 -> Error.spotifyRateLimitExceeded();
            default -> Error.unknownError("streamingData", "Failed to get streamingData");
        };
    }
}
