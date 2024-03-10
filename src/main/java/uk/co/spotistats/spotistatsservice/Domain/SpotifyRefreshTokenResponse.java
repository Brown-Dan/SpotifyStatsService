package uk.co.spotistats.spotistatsservice.Domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyRefreshTokenResponse(@JsonProperty("access_token") String accessToken) {
}
