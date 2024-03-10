package uk.co.spotistats.spotistatsservice.Domain;

import java.time.LocalDateTime;

import static uk.co.spotistats.spotistatsservice.Domain.SpotifyAuthData.Builder.someUserAuthData;

public record SpotifyAuthData(String username, String refreshToken, String accessToken, LocalDateTime lastUpdated) {

    public boolean hasValidAccessToken(){
        return lastUpdated.isBefore(LocalDateTime.now().minusHours(1));
    }

    public SpotifyAuthData updateFromRefreshResponse(SpotifyRefreshTokenResponse spotifyRefreshTokenResponse){
        return someUserAuthData()
                .withUsername(username)
                .withLastUpdated(lastUpdated)
                .withRefreshToken(refreshToken)
                .withAccessToken(spotifyRefreshTokenResponse.accessToken())
                .build();
    }

    public static final class Builder {
        private String username;
        private String refreshToken;
        private String accessToken;
        private LocalDateTime lastUpdated;

        private Builder() {
        }

        public static Builder someUserAuthData() {
            return new Builder();
        }

        public Builder withUsername(String username) {
            this.username = username;
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
            return new SpotifyAuthData(username, refreshToken, accessToken, lastUpdated);
        }
    }
}
