package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks;

import java.time.LocalDateTime;

public record RankedTrackDataResource(int ranking,
                                      String trackName,
                                      String artistName,
                                      String albumName,
                                      String trackUri,
                                      LocalDateTime lastStreamedDate,
                                      int totalMinutesPlayed,
                                      int totalMsPlayed,
                                      int totalStreams) {


    public static final class Builder {
        private int ranking;
        private String trackName;
        private String artistName;
        private String albumName;
        private String trackUri;
        private LocalDateTime lastStreamedDate;
        private int totalMinutesPlayed;
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

        public Builder withTotalMinutesPlayed(int totalMinutesPlayed) {
            this.totalMinutesPlayed = totalMinutesPlayed;
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

        public RankedTrackDataResource build() {
            return new RankedTrackDataResource(ranking, trackName, artistName, albumName, trackUri, lastStreamedDate, totalMinutesPlayed, totalMsPlayed, totalStreams);
        }
    }
}
