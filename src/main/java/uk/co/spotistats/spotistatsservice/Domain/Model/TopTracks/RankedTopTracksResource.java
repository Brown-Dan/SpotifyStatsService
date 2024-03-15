package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks;

import uk.co.spotistats.spotistatsservice.Domain.Model.RankedTrackData;

import java.util.List;

public record RankedTopTracksResource(List<RankedTrackData> rankedTrackData, int totalResults,
                                      int page) implements TopTracksResource {

    public static final class Builder {
        private List<RankedTrackData> rankedTrackData;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder aRankedTopTracksResponse() {
            return new Builder();
        }

        public Builder withRankedStreamData(List<RankedTrackData> rankedTrackData) {
            this.rankedTrackData = rankedTrackData;
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
            return new RankedTopTracksResource(rankedTrackData, totalResults, page);
        }
    }
}
