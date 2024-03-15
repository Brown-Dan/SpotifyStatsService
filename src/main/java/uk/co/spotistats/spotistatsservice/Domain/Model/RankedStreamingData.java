package uk.co.spotistats.spotistatsservice.Domain.Model;

import java.util.List;

public record RankedStreamingData(List<RankedTrackData> rankedTrackData, Integer size) {

    public static final class Builder {
        private List<RankedTrackData> rankedTrackData;
        private Integer size;

        private Builder() {
        }

        public static Builder aRankedStreamingData() {
            return new Builder();
        }

        public Builder withRankedStreamData(List<RankedTrackData> rankedTrackData) {
            this.rankedTrackData = rankedTrackData;
            return this;
        }

        public Builder withSize(Integer size) {
            this.size = size;
            return this;
        }

        public RankedStreamingData build() {
            return new RankedStreamingData(rankedTrackData, size);
        }
    }
}
