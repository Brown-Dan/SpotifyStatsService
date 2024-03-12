package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Repository;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.Response;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
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

    public Result<StreamingData, Error> getRecentStreamingData(SpotifyAuthData spotifyAuthData) {
        Response<JSONObject> response = traverson.from(RECENT_STREAMS_URL)
                .withHeader("Authorization", "Bearer %s ".formatted(spotifyAuthData.accessToken()))
                .withQueryParam("limit", "50")
                .withQueryParam("before", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString())
                .get();
        if (response.isSuccessful()) {
            return spotifyResponseJsonToStreamingDataMapper.mapFromRecentStreamsJson(response.getResource());
        }
        return new Result.Failure<>(responseToError(response, spotifyAuthData.username()));
    }

    public Result<StreamingData, Error> getTopTracks(SpotifyAuthData spotifyAuthData) {
        Response<JSONObject> response = traverson.from(TOP_TRACKS_URL)
                .withHeader("Authorization", "Bearer %s ".formatted(spotifyAuthData.accessToken()))
                .withQueryParam("limit", "50")
                .withQueryParam("time_range", "long_term")
                .get();
        if (response.isSuccessful()) {
            return spotifyResponseJsonToStreamingDataMapper.mapFromTopStreamsJson(response.getResource());
        }
        return new Result.Failure<>(responseToError(response, spotifyAuthData.username()));
    }

    private Error responseToError(Response<JSONObject> response, String username) {
        return switch (response.getStatusCode()) {
            case 401, 403 -> Error.notFound("spotifyAuthDetails", username);
            case 429 -> Error.spotifyRateLimitExceeded();
            default -> Error.unknownError("streamingData", "Failed to get streamingData");
        };
    }
}
