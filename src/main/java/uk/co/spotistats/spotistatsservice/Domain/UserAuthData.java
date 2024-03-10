package uk.co.spotistats.spotistatsservice.Domain;

import java.time.LocalDateTime;

public record UserAuthData(String username, String refreshToken, String accessToken, LocalDateTime lastUpdated) {

    public static final class Builder {
        private String username;
        private String refreshToken;
        private String accessToken;
        private LocalDateTime lastUpdated;

        private Builder() {
        }

        public static Builder aUser() {
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

        public UserAuthData build() {
            return new UserAuthData(username, refreshToken, accessToken, lastUpdated);
        }
    }
}
