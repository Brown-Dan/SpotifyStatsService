package uk.co.spotistats.spotistatsservice.Domain.Model;

import java.util.List;

public record RankedStreamingData(List<RankedStreamData> rankedStreamData, Integer size) {

    public static final class Builder {
        private List<RankedStreamData> rankedStreamData;
        private Integer size;

        private Builder() {
        }

        public static Builder aRankedStreamingData() {
            return new Builder();
        }

        public Builder withRankedStreamData(List<RankedStreamData> rankedStreamData) {
            this.rankedStreamData = rankedStreamData;
            return this;
        }

        public Builder withSize(Integer size) {
            this.size = size;
            return this;
        }

        public RankedStreamingData build() {
            return new RankedStreamingData(rankedStreamData, size);
        }
    }
}
