package uk.co.spotistats.spotistatsservice.Domain.Response.Upload;

import java.time.LocalDateTime;

public record StreamingDataUpsertResult(String username, Integer size,
                                        LocalDateTime firstStreamDateTime,
                                        LocalDateTime lastStreamDateTime) {


    public static final class Builder {
        private String username;
        private Integer size;
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

        public Builder withSize(Integer size) {
            this.size = size;
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
            return new StreamingDataUpsertResult(username, size, firstStreamDateTime, lastStreamDateTime);
        }
    }
}
