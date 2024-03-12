package uk.co.spotistats.spotistatsservice.Domain.Model;

import java.time.LocalDateTime;
import java.util.List;

import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.aStreamingData;

public record StreamingData(List<StreamData> streamData,
                            Integer size,
                            LocalDateTime firstStreamDateTime,
                            LocalDateTime lastStreamDateTime) {

    public static StreamingData fromStreamingDataEntity(uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity) {
        return aStreamingData()
                .withFirstStreamDateTime(streamingDataEntity.getFirstStreamDate())
                .withLastStreamDateTime(streamingDataEntity.getLastStreamData())
                .withSize(streamingDataEntity.getStreamCount())
                .build();
    }

    public static final class Builder {
        private List<StreamData> streamData;
        private Integer size;
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

        public StreamingData build() {
            return new StreamingData(streamData, size, firstStreamDateTime, lastStreamDateTime);
        }
    }
}
