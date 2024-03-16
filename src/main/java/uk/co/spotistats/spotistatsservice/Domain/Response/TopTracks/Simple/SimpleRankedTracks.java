package uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Simple;

import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracksResource;

import java.util.List;

public record SimpleRankedTracks(List<SimpleRankedTrack> tracks, int totalResults,
                                 int page) implements TopTracksResource {

    public static final class Builder {
        private List<SimpleRankedTrack> tracks;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder someSimpleRankedTracks() {
            return new Builder();
        }

        public Builder withStreamData(List<SimpleRankedTrack> tracks) {
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

        public SimpleRankedTracks build() {
            return new SimpleRankedTracks(tracks, totalResults, page);
        }
    }
}