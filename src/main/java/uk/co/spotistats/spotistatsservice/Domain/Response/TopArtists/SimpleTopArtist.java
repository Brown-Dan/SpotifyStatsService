package uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists;

import java.util.List;

public record SimpleTopArtist(String name, String spotifyUri, Integer popularity, String spotifyProfileLink, List<String> genres) {

    public static final class Builder {
        private String name;
        private String spotifyUri;
        private Integer popularity;
        private String spotifyProfileLink;
        private List<String> genres;

        private Builder() {
        }

        public static Builder aSimpleTopArtist() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSpotifyUri(String spotifyUri) {
            this.spotifyUri = spotifyUri;
            return this;
        }

        public Builder withPopularity(Integer popularity) {
            this.popularity = popularity;
            return this;
        }

        public Builder withSpotifyProfileLink(String spotifyProfileLink) {
            this.spotifyProfileLink = spotifyProfileLink;
            return this;
        }

        public Builder withGenres(List<String> genres) {
            this.genres = genres;
            return this;
        }

        public SimpleTopArtist build() {
            return new SimpleTopArtist(name, spotifyUri, popularity, spotifyProfileLink, genres);
        }
    }
}
