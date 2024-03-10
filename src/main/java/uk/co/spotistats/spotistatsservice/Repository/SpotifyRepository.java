package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Repository;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.Response;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Repository.Mapper.PlayHistoryJsonToStreamingDataMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
public class SpotifyRepository {

    private final Traverson traverson;
    private final PlayHistoryJsonToStreamingDataMapper playHistoryJsonToStreamingDataMapper;

    private static final String RECENT_STREAMS_URL = "https://api.spotify.com/v1/me/player/recently-played";

    public SpotifyRepository(Traverson traverson, PlayHistoryJsonToStreamingDataMapper playHistoryJsonToStreamingDataMapper) {
        this.traverson = traverson;
        this.playHistoryJsonToStreamingDataMapper = playHistoryJsonToStreamingDataMapper;
    }

    public Result<StreamingData, Error> getRecentStreamingData(SpotifyAuthData spotifyAuthData) {
        Response<JSONObject> response = traverson.from(RECENT_STREAMS_URL)
                .withHeader("Authorization", "Bearer %s ".formatted(spotifyAuthData.accessToken()))
                .withQueryParam("limit", "50")
                .withQueryParam("before", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString())
                .get();
        if (response.isSuccessful()) {
            return playHistoryJsonToStreamingDataMapper.map(response.getResource());
        }
        return new Result.Failure<>(responseToError(response));
    }

    private Error responseToError(Response<JSONObject> response) {
        return switch (response.getStatusCode()) {
            case 401 -> new Error("User needs to reauthenticate");
            case 403 -> new Error("User is not authenticated");
            case 429 -> new Error("App exceeding rate limit");
            default -> new Error("Failed to getRecentStreamingData");
        };
    }
}