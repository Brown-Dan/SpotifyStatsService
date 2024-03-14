package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

public record SpotifySearchRequest(String userId, Integer limit, SpotifyAuthData authData, boolean createPlaylist) {

    public static final class Builder {
        private String userId;
        private Integer limit;
        private SpotifyAuthData authData;
        private Boolean createPlaylist;

        private Builder() {
        }

        public static Builder aSpotifySearchRequest() {
            return new Builder();
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
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

        public Builder withCreatePlaylist(Boolean createPlaylist) {
            this.createPlaylist = createPlaylist;
            return this;
        }

        public SpotifySearchRequest build() {
            return new SpotifySearchRequest(userId, limit, authData, createPlaylist);
        }
    }
    public Builder cloneBuilder() {
        return Builder.aSpotifySearchRequest()
                .withUserId(userId)
                .withLimit(limit)
                .withCreatePlaylist(createPlaylist)
                .withAuthData(authData);
    }
}
