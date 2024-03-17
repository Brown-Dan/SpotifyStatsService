package uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists;

import java.util.List;

public record AdvancedTopArtists(List<AdvancedTopArtist> artists, int totalResults, int page) implements TopArtists {

    public static final class Builder {
        private List<AdvancedTopArtist> artists;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder someAdvancedTopArtists() {
            return new Builder();
        }

        public Builder withArtists(List<AdvancedTopArtist> artists) {
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

        public AdvancedTopArtists build() {
            return new AdvancedTopArtists(artists, totalResults, page);
        }
    }
}
