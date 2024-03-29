package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

import static uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest.Builder.aTopTracksSearchRequest;

public record TopTracksSearchRequest(String userId, Integer limit, SpotifyAuthData authData, Boolean createPlaylist,
                                     Integer page, Boolean advanced) {


    public static final class Builder {
        private String userId;
        private Integer limit;
        private SpotifyAuthData authData;
        private Boolean createPlaylist;
        private Integer page;
        private Boolean advanced;

        private Builder() {
        }

        public static Builder aTopTracksSearchRequest() {
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

        public Builder withAdvanced(Boolean advanced) {
            this.advanced = advanced;
            return this;
        }

        public TopTracksSearchRequest build() {
            return new TopTracksSearchRequest(userId, limit, authData, createPlaylist, page, advanced);
        }
    }

    public TopTracksSearchRequest.Builder cloneBuilder() {
        return aTopTracksSearchRequest()
                .withUserId(userId)
                .withLimit(limit)
                .withAuthData(authData)
                .withCreatePlaylist(createPlaylist)
                .withPage(page)
                .withAdvanced(advanced);
    }
}
