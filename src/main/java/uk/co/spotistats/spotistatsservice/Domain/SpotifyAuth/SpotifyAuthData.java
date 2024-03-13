package uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import static uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData.Builder.someSpotifyAuthData;

public record SpotifyAuthData(String userId, @JsonProperty("refresh_token") String refreshToken, @JsonProperty("access_token") String accessToken, LocalDateTime lastUpdated) {

    public boolean hasValidAccessToken() {
        return !lastUpdated.isBefore(LocalDateTime.now().minusHours(1));
    }

    public SpotifyAuthData updateFromRefreshResponse(SpotifyRefreshTokenResponse spotifyRefreshTokenResponse) {
        return someSpotifyAuthData()
                .withUserId(userId)
                .withLastUpdated(lastUpdated)
                .withRefreshToken(refreshToken)
                .withAccessToken(spotifyRefreshTokenResponse.accessToken())
                .build();
    }

    public static final class Builder {
        private String userId;
        private String refreshToken;
        private String accessToken;
        private LocalDateTime lastUpdated;

        private Builder() {
        }

        public static Builder someSpotifyAuthData() {
            return new Builder();
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withLastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public SpotifyAuthData build() {
            return new SpotifyAuthData(userId, refreshToken, accessToken, lastUpdated);
        }
    }

    public SpotifyAuthData.Builder cloneBuilder(){
        return someSpotifyAuthData()
                .withAccessToken(accessToken)
                .withLastUpdated(lastUpdated)
                .withUserId(userId)
                .withRefreshToken(refreshToken);
    }
}
