package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks;

import java.util.List;

public record RankedTopTracksResource(List<RankedTrackDataResource> tracks, int totalResults,
                                      int page) implements TopTracksResource {

    public static final class Builder {
        private List<RankedTrackDataResource> tracks;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder aRankedTopTracksResponse() {
            return new Builder();
        }

        public Builder withTracks(List<RankedTrackDataResource> tracks) {
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

        public RankedTopTracksResource build() {
            return new RankedTopTracksResource(tracks, totalResults, page);
        }
    }
}
