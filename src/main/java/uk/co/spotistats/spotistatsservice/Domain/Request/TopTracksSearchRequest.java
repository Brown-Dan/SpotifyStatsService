package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

import static uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest.Builder.aTopTracksSearchRequest;

public record TopTracksSearchRequest(String userId, int limit, SpotifyAuthData authData, boolean createPlaylist,
                                     int page, boolean ranked) {


    public static final class Builder {
        private String userId;
        private int limit;
        private SpotifyAuthData authData;
        private boolean createPlaylist;
        private int page;
        private boolean ranked;

        private Builder() {
        }

        public static Builder aTopTracksSearchRequest() {
            return new Builder();
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder withAuthData(SpotifyAuthData authData) {
            this.authData = authData;
            return this;
        }

        public Builder withCreatePlaylist(boolean createPlaylist) {
            this.createPlaylist = createPlaylist;
            return this;
        }

        public Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public Builder withRanked(boolean ranked) {
            this.ranked = ranked;
            return this;
        }

        public TopTracksSearchRequest build() {
            return new TopTracksSearchRequest(userId, limit, authData, createPlaylist, page, ranked);
        }
    }

    public TopTracksSearchRequest.Builder cloneBuilder() {
        return aTopTracksSearchRequest()
                .withUserId(userId)
                .withLimit(limit)
                .withAuthData(authData)
                .withCreatePlaylist(createPlaylist)
                .withPage(page)
                .withRanked(ranked);
    }
}
