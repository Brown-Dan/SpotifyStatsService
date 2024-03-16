package uk.co.spotistats.spotistatsservice.Domain.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;

import java.time.LocalDateTime;

import static uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack.Builder.anAdvancedTrack;

public record AdvancedTrack(
        @JsonProperty String trackName,
        @JsonProperty String artistName,
        @JsonProperty String albumName,
        @JsonProperty String trackUri,
        @JsonProperty LocalDateTime firstStreamDate,
        @JsonProperty LocalDateTime lastStreamedDate,
        @JsonProperty int totalMinutesPlayed,
        @JsonProperty long totalMsPlayed,
        @JsonProperty int totalStreams) {


    public static AdvancedTrack fromStreamingData(StreamingData streamingData) {
        if (streamingData.streamData().isEmpty()){
            return AdvancedTrack.Builder.anAdvancedTrack().build();
        }
        long totalMsPlayed = streamingData.streamData().stream().mapToLong(StreamData::timeStreamed).sum();
        StreamData track = streamingData.streamData().getFirst();
        return anAdvancedTrack()
                .withAlbumName(track.album())
                .withTrackName(track.name())
                .withArtistName(track.artist())
                .withTrackUri(track.trackUri())
                .withFirstStreamDate(streamingData.firstStreamDateTime())
                .withLastStreamedDate(streamingData.lastStreamDateTime())
                .withTotalMsPlayed(totalMsPlayed)
                .withTotalStreams(streamingData.streamData().size())
                .withTotalMinutesPlayed(((int) totalMsPlayed / 1000) / 60)
                .build();
    }

    public static final class Builder {
        private String trackName;
        private String artistName;
        private String albumName;
        private String trackUri;
        private LocalDateTime firstStreamDate;
        private LocalDateTime lastStreamedDate;
        private int totalMinutesPlayed;
        private long totalMsPlayed;
        private int totalStreams;

        private Builder() {
        }

        public static Builder anAdvancedTrack() {
            return new Builder();
        }

        public Builder withTrackName(String trackName) {
            this.trackName = trackName;
            return this;
        }

        public Builder withArtistName(String artistName) {
            this.artistName = artistName;
            return this;
        }

        public Builder withAlbumName(String albumName) {
            this.albumName = albumName;
            return this;
        }

        public Builder withTrackUri(String trackUri) {
            this.trackUri = trackUri;
            return this;
        }

        public Builder withFirstStreamDate(LocalDateTime firstStreamDate) {
            this.firstStreamDate = firstStreamDate;
            return this;
        }

        public Builder withLastStreamedDate(LocalDateTime lastStreamedDate) {
            this.lastStreamedDate = lastStreamedDate;
            return this;
        }

        public Builder withTotalMinutesPlayed(int totalMinutesPlayed) {
            this.totalMinutesPlayed = totalMinutesPlayed;
            return this;
        }

        public Builder withTotalMsPlayed(long totalMsPlayed) {
            this.totalMsPlayed = totalMsPlayed;
            return this;
        }

        public Builder withTotalStreams(int totalStreams) {
            this.totalStreams = totalStreams;
            return this;
        }

        public AdvancedTrack build() {
            return new AdvancedTrack(trackName, artistName, albumName, trackUri, firstStreamDate, lastStreamedDate, totalMinutesPlayed, totalMsPlayed, totalStreams);
        }
    }
}
