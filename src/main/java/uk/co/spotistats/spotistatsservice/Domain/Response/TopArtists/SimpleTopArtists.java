package uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists;

import java.util.List;

public record SimpleTopArtists(List<SimpleTopArtist> artists, int totalResults, int page) implements TopArtists {

    public static final class Builder {
        private List<SimpleTopArtist> artists;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder aSimpleTopArtists() {
            return new Builder();
        }

        public Builder withArtists(List<SimpleTopArtist> artists) {
            this.artists = artists;
            return this;
        }

        public Builder withTotalResults(int totalResults) {
            this.totalResults = totalResults;
            return this;
        }

        public Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public SimpleTopArtists build() {
            return new SimpleTopArtists(artists, totalResults, page);
        }
    }
}
