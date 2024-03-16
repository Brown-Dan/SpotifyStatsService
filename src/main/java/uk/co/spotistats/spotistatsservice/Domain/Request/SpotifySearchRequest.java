package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

public record SpotifySearchRequest(String userId, Integer limit, SpotifyAuthData authData, Boolean createPlaylist,
                                   Integer page) {

    public static final class Builder {
        private String userId;
        private Integer limit;
        private SpotifyAuthData authData;
        private Boolean createPlaylist;
        private Integer page;

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

        public Builder withPage(Integer page) {
            this.page = page;
            return this;
        }

        public SpotifySearchRequest build() {
            return new SpotifySearchRequest(userId, limit, authData, createPlaylist, page);
        }
    }

    public Builder cloneBuilder() {
        return Builder.aSpotifySearchRequest()
                .withUserId(userId)
                .withLimit(limit)
                .withPage(page)
                .withCreatePlaylist(createPlaylist)
                .withAuthData(authData);
    }
}
