package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

public record SpotifySearchRequest(String username, Integer limit, SpotifyAuthData authData) {


    public static final class Builder {

        private String username;
        private Integer limit;
        private SpotifyAuthData authData;

        private Builder() {
        }

        public static Builder aSpotifySearchRequest() {
            return new Builder();
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder withAuthData(SpotifyAuthData authData) {
            this.authData = authData;
            return this;
        }

        public SpotifySearchRequest build() {
            return new SpotifySearchRequest(username, limit, authData);
        }

    }

    public Builder cloneBuilder() {
        return Builder.aSpotifySearchRequest()
                .withUsername(username)
                .withLimit(limit)
                .withAuthData(authData);
    }
}
