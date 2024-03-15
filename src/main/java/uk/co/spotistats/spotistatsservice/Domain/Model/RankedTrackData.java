package uk.co.spotistats.spotistatsservice.Domain.Model;

import java.time.LocalDateTime;

public record RankedTrackData(int ranking,
                              String trackName,
                              String artistName,
                              String albumName,
                              String trackUri,
                              LocalDateTime lastStreamedDate,
                              int minutesPlayed,
                              int totalMsPlayed,
                              int totalStreams) {


    public static final class Builder {
        private int ranking;
        private String trackName;
        private String artistName;
        private String albumName;
        private String trackUri;
        private LocalDateTime lastStreamedDate;
        private int minutesPlayed;
        private int totalMsPlayed;
        private int totalStreams;

        private Builder() {
        }

        public static Builder aRankedStreamData() {
            return new Builder();
        }

        public Builder withRanking(int ranking) {
            this.ranking = ranking;
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

        public Builder withTrackUri(String trackUri) {
            this.trackUri = trackUri;
            return this;
        }

        public Builder withLastStreamedDate(LocalDateTime lastStreamedDate) {
            this.lastStreamedDate = lastStreamedDate;
            return this;
        }

        public Builder withMinutesPlayed(int minutesPlayed) {
            this.minutesPlayed = minutesPlayed;
            return this;
        }

        public Builder withTotalMsPlayed(int totalMsPlayed) {
            this.totalMsPlayed = totalMsPlayed;
            return this;
        }

        public Builder withTotalStreams(int totalStreams) {
            this.totalStreams = totalStreams;
            return this;
        }

        public RankedTrackData build() {
            return new RankedTrackData(ranking, trackName, artistName, albumName, trackUri, lastStreamedDate, minutesPlayed, totalMsPlayed, totalStreams);
        }
    }
}
