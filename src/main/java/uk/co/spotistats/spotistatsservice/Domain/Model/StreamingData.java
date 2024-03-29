package uk.co.spotistats.spotistatsservice.Domain.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;

import java.time.LocalDateTime;
import java.util.List;

import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.someStreamingData;

public record StreamingData(List<StreamData> streamData,
                            Integer size,
                            LocalDateTime firstStreamDateTime,
                            LocalDateTime lastStreamDateTime, LocalDateTime lastUpdated, @JsonIgnore String username) {

    public static StreamingData fromStreamingDataEntity(uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity) {
        return someStreamingData()
                .withUsername(streamingDataEntity.getUsername())
                .withFirstStreamDateTime(streamingDataEntity.getFirstStreamDate())
                .withLastStreamDateTime(streamingDataEntity.getLastStreamData())
                .withSize(streamingDataEntity.getStreamCount())
                .withLastUpdated(streamingDataEntity.getLastUpdated())
                .build();
    }

    public boolean shouldSync() {
        return lastUpdated.isBefore(LocalDateTime.now().minusMinutes(25));
    }

    public StreamingData updateStreamingDataFromSync(RecentTracks recentTracks) {
        LocalDateTime firstStreamDateTime = firstStreamDateTime().isBefore(recentTracks.firstStreamDateTime())
                ? firstStreamDateTime() : recentTracks.firstStreamDateTime();
        LocalDateTime lastStreamDateTime = lastStreamDateTime().isAfter(recentTracks.lastStreamDateTime())
                ? lastStreamDateTime() : recentTracks.lastStreamDateTime();

        return someStreamingData()
                .withSize(size() + recentTracks.size())
                .withFirstStreamDateTime(firstStreamDateTime)
                .withLastStreamDateTime(lastStreamDateTime)
                .withUsername(username)
                .build();
    }

    public static final class Builder {
        private List<StreamData> streamData;
        private Integer size;
        private LocalDateTime firstStreamDateTime;
        private LocalDateTime lastStreamDateTime;
        private LocalDateTime lastUpdated;
        private String username;

        private Builder() {
        }

        public static Builder someStreamingData() {
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

        public Builder withLastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public StreamingData build() {
            return new StreamingData(streamData, size, firstStreamDateTime, lastStreamDateTime, lastUpdated, username);
        }
    }

    public StreamingData.Builder cloneBuilder() {
        return someStreamingData()
                .withUsername(username)
                .withLastUpdated(lastUpdated)
                .withStreamData(streamData)
                .withSize(size)
                .withLastStreamDateTime(lastStreamDateTime)
                .withFirstStreamDateTime(firstStreamDateTime);
    }
}
