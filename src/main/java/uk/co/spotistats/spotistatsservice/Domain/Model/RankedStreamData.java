package uk.co.spotistats.spotistatsservice.Domain.Model;

import java.time.LocalDateTime;

public record RankedStreamData(LocalDateTime lastStreamDateTime,
                               int totalMsPlayed,
                               String trackUri,
                               String trackName,
                               String artistName,
                               String albumName,
                               int ranking,
                               int minutesPlayed,
                               int totalStreams) {

    public static final class Builder {
        private LocalDateTime lastStreamDateTime;
        private int totalMsPlayed;
        private String trackUri;
        private String trackName;
        private String artistName;
        private String albumName;
        private int ranking;
        private int minutesPlayed;
        private int totalStreams;

        private Builder() {
        }

        public static Builder aRankedStreamData() {
            return new Builder();
        }

        public Builder withLastStreamDateTime(LocalDateTime lastStreamDateTime) {
            this.lastStreamDateTime = lastStreamDateTime;
            return this;
        }

        public Builder withTotalMsPlayed(int totalMsPlayed) {
            this.totalMsPlayed = totalMsPlayed;
            return this;
        }

        public Builder withTrackUri(String trackUri) {
            this.trackUri = trackUri;
            return this;
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

        public Builder withRanking(int ranking) {
            this.ranking = ranking;
            return this;
        }

        public Builder withMinutesPlayed(int minutesPlayed) {
            this.minutesPlayed = minutesPlayed;
            return this;
        }

        public Builder withTotalStreams(int totalStreams) {
            this.totalStreams = totalStreams;
            return this;
        }

        public RankedStreamData build() {
            return new RankedStreamData(lastStreamDateTime, totalMsPlayed, trackUri, trackName, artistName, albumName, ranking, minutesPlayed, totalStreams);
        }
    }
}
