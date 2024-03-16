package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Simple;

import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.TopTracksResource;

import java.util.List;

public record SimpleTopTracksResource(List<SimpleTrackDataResource> tracks, int totalResults,
                                      int page) implements TopTracksResource {

    public static final class Builder {
        private List<SimpleTrackDataResource> tracks;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder aSimpleTopTracksResponse() {
            return new Builder();
        }

        public Builder withStreamData(List<SimpleTrackDataResource> tracks) {
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

        public SimpleTopTracksResource build() {
            return new SimpleTopTracksResource(tracks, totalResults, page);
        }
    }
}