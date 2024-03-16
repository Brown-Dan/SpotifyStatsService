package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

import static uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest.Builder.aRecentTracksSearchRequest;

public record RecentTracksSearchRequest(String userId, Integer limit, SpotifyAuthData authData, Boolean createPlaylist) {

    public static final class Builder {
        private String userId;
        private Integer limit;
        private SpotifyAuthData authData;
        private Boolean createPlaylist;

        private Builder() {
        }

        public static Builder aRecentTracksSearchRequest() {
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

        public RecentTracksSearchRequest build() {
            return new RecentTracksSearchRequest(userId, limit, authData, createPlaylist);
        }
    }

    public RecentTracksSearchRequest.Builder cloneBuilder(){
        return aRecentTracksSearchRequest()
                .withAuthData(authData)
                .withUserId(userId)
                .withCreatePlaylist(createPlaylist)
                .withLimit(limit);
    }
}
