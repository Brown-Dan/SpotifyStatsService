package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks;

import java.util.List;

public record UnrankedTopTracksResource(List<UnrankedTrackDataResource> streamData, int totalResults,
                                        int page) implements TopTracksResource {

    public static final class Builder {
        private List<UnrankedTrackDataResource> streamData;
        private int totalResults;
        private int page;

        private Builder() {
        }

        public static Builder anUnrankedTopTracksResponse() {
            return new Builder();
        }

        public Builder withStreamData(List<UnrankedTrackDataResource> streamData) {
            this.streamData = streamData;
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
            return new UnrankedTopTracksResource(streamData, totalResults, page);
        }
    }
}