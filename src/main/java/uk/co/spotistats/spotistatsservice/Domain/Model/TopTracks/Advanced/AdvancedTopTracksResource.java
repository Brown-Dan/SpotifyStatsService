package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Advanced;

import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.TopTracksResource;

import java.util.List;

public record AdvancedTopTracksResource(List<AdvancedTrackDataResource> tracks, int totalResults,
                                        int page) implements TopTracksResource {

    public static final class Builder {
        private List<AdvancedTrackDataResource> tracks;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder anAdvancedTopTracksResource() {
            return new Builder();
        }

        public Builder withTracks(List<AdvancedTrackDataResource> tracks) {
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

        public AdvancedTopTracksResource build() {
            return new AdvancedTopTracksResource(tracks, totalResults, page);
        }
    }
}
