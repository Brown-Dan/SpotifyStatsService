package uk.co.spotistats.spotistatsservice.Domain.Response;

import java.time.LocalDateTime;

public record UserDataUploadResponse(Integer songCount, LocalDateTime dataStartDate, LocalDateTime dataEndDate) {

    public static final class Builder {
        private Integer songCount;
        private LocalDateTime dataStartDate;
        private LocalDateTime dataEndDate;

        private Builder() {
        }

        public static Builder anUserDataUploadResponse() {
            return new Builder();
        }

        public Builder withSongCount(Integer songCount) {
            this.songCount = songCount;
            return this;
        }

        public Builder withDataStartDate(LocalDateTime dataStartDate) {
            this.dataStartDate = dataStartDate;
            return this;
        }

        public Builder withDataEndDate(LocalDateTime dataEndDate) {
            this.dataEndDate = dataEndDate;
            return this;
        }

        public UserDataUploadResponse build() {
            return new UserDataUploadResponse(songCount, dataStartDate, dataEndDate);
        }
    }
}
