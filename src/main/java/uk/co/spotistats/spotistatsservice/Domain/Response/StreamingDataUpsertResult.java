package uk.co.spotistats.spotistatsservice.Domain.Response;

import java.time.LocalDateTime;

public record StreamingDataUpsertResult(String username, Integer totalStreams,
                                        LocalDateTime firstStreamDateTime,
                                        LocalDateTime lastStreamDateTime) {


    public static final class Builder {
        private String username;
        private Integer totalStreams;
        private LocalDateTime firstStreamDateTime;
        private LocalDateTime lastStreamDateTime;

        private Builder() {
        }

        public static Builder aStreamingDataInsertResult() {
            return new Builder();
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withTotalStreams(Integer totalStreams) {
            this.totalStreams = totalStreams;
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

        public StreamingDataUpsertResult build() {
            return new StreamingDataUpsertResult(username, totalStreams, firstStreamDateTime, lastStreamDateTime);
        }
    }
}
