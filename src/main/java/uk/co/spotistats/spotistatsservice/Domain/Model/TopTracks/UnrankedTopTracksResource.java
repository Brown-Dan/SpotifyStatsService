package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks;

import java.util.List;

public record UnrankedTopTracksResource(List<UnrankedTrackDataResource> tracks, int totalResults,
                                        int page) implements TopTracksResource {

    public static final class Builder {
        private List<UnrankedTrackDataResource> tracks;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder anUnrankedTopTracksResponse() {
            return new Builder();
        }

        public Builder withStreamData(List<UnrankedTrackDataResource> tracks) {
            this.tracks = tracks;
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

        public UnrankedTopTracksResource build() {
            return new UnrankedTopTracksResource(tracks, totalResults, page);
        }
    }
}