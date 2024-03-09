package uk.co.spotistats.spotistatsservice.Domain;

import java.time.LocalDateTime;
import java.util.List;

public record StreamingData(List<StreamData> streamData,
                            Integer streamCount,
                            LocalDateTime firstStreamDateTime,
                            LocalDateTime lastStreamDateTime) {

    public static final class Builder {
        private List<StreamData> streamData;
        private Integer streamCount;
        private LocalDateTime firstStreamDateTime;
        private LocalDateTime lastStreamDateTime;

        private Builder() {
        }

        public static Builder aStreamingData() {
            return new Builder();
        }

        public Builder withStreamData(List<StreamData> streamData) {
            this.streamData = streamData;
            return this;
        }

        public Builder withTotalStreams(Integer streamCount) {
            this.streamCount = streamCount;
            return this;
        }

        public Builder withFirstStreamDateTime(LocalDateTime firstStreamDateTime) {
            this.firstStreamDateTime = firstStreamDateTime;
            return this;
        }

        public Builder withLastStreamDateTime(LocalDateTime lastStreamDateTime) {
            this.lastStreamDateTime = lastStreamDateTime;
            return this;
        }

        public StreamingData build() {
            return new StreamingData(streamData, streamCount, firstStreamDateTime, lastStreamDateTime);
        }
    }
}
