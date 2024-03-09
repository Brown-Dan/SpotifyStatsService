package uk.co.spotistats.spotistatsservice.Domain.Response;

import java.time.LocalDateTime;

public record StreamingDataInsertResult(Integer streamCount,
                                        LocalDateTime firstStreamDateTime,
                                        LocalDateTime lastStreamDateTime) {

    public static final class Builder {
        private Integer streamCount;
        private LocalDateTime firstStreamDateTime;
        private LocalDateTime lastStreamDateTime;

        private Builder() {
        }

        public static Builder aStreamingDataInsertResult() {
            return new Builder();
        }

        public Builder withStreamCount(Integer streamCount) {
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

        public StreamingDataInsertResult build() {
            return new StreamingDataInsertResult(streamCount, firstStreamDateTime, lastStreamDateTime);
        }
    }
}
