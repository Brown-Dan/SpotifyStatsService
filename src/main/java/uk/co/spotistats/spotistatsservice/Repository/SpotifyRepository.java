package uk.co.spotistats.spotistatsservice.Repository;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Repository;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.Response;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
public class SpotifyRepository {

    private final Traverson traverson;

    private static final String RECENT_STREAMS_URL = "https://api.spotify.com/v1/me/player/recently-played";

    public SpotifyRepository(Traverson traverson) {
        this.traverson = traverson;
    }

    public Result<StreamingData, Error> getRecentStreamingData(SpotifyAuthData spotifyAuthData){
        Response<JSONObject> response = traverson.from(RECENT_STREAMS_URL)
                .withHeader("Authorization", "Bearer %s ".formatted(spotifyAuthData.accessToken()))
                .withQueryParam("limit", "50")
                .withQueryParam("before", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString())
                .get();
        if (response.isSuccessful()){
            return null;
        }
        if (response.getStatusCode() == 401){
            return new Result.Failure<>(new Error("User reauthenticate required"));
        }
        return new Result.Failure<>(new Error("Failed to retrieve data - temp"));
    }


}
