package uk.co.spotistats.spotistatsservice.Domain.Model;

import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.RankedTrackDataResource;

import java.util.List;

public record RankedStreamingData(List<RankedTrackDataResource> rankedTracks, Integer size) {

    public static final class Builder {
        private List<RankedTrackDataResource> rankedTrackDatumResources;
        private Integer size;

        private Builder() {
        }

        public static Builder aRankedStreamingData() {
            return new Builder();
        }

        public Builder withRankedStreamData(List<RankedTrackDataResource> rankedTrackDatumResources) {
            this.rankedTrackDatumResources = rankedTrackDatumResources;
            return this;
        }

        public Builder withSize(Integer size) {
            this.size = size;
            return this;
        }

        public RankedStreamingData build() {
            return new RankedStreamingData(rankedTrackDatumResources, size);
        }
    }
}
