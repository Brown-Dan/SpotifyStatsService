package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

import static uk.co.spotistats.spotistatsservice.Domain.Request.TopArtistsSearchRequest.Builder.aTopArtistsSearchRequest;

public record TopArtistsSearchRequest(String userId, Integer limit, SpotifyAuthData authData,
                                      Integer page, Boolean advanced) {

    public static final class Builder {
        private String userId;
        private Integer limit;
        private SpotifyAuthData authData;
        private Integer page;
        private Boolean advanced;

        private Builder() {
        }

        public static Builder aTopArtistsSearchRequest() {
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

        public Builder withPage(Integer page) {
            this.page = page;
            return this;
        }

        public Builder withAdvanced(Boolean advanced) {
            this.advanced = advanced;
            return this;
        }

        public TopArtistsSearchRequest build() {
            return new TopArtistsSearchRequest(userId, limit, authData, page, advanced);
        }
    }

    public TopArtistsSearchRequest.Builder cloneBuilder(){
        return aTopArtistsSearchRequest()
                .withUserId(userId)
                .withLimit(limit)
                .withAuthData(authData)
                .withPage(page)
                .withAdvanced(advanced);
    }
}
