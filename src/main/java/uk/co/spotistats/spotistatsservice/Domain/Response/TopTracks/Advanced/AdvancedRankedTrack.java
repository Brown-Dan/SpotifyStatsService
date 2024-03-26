package uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Advanced;

import uk.co.spotistats.spotistatsservice.Domain.Model.Image;

import java.time.LocalDateTime;

public record AdvancedRankedTrack(int ranking,
                                  String trackName,
                                  String artistName,
                                  String albumName,
                                  String trackUri,
                                  LocalDateTime firstStreamedDate,
                                  LocalDateTime lastStreamedDate,
                                  int totalMinutesPlayed,
                                  int totalMsPlayed,
                                  int totalStreams,
                                  Image imageUrl
) {

    public static final class Builder {
        private int ranking;
        private String trackName;
        private String artistName;
        private String albumName;
        private String trackUri;
        private LocalDateTime firstStreamedDate;
        private LocalDateTime lastStreamedDate;
        private int totalMinutesPlayed;
        private int totalMsPlayed;
        private int totalStreams;
        private Image imageUrl;

        private Builder() {
        }

        public static Builder anAdvancedRankedTrack() {
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

        public Builder withFirstStreamedDate(LocalDateTime firstStreamedDate) {
            this.firstStreamedDate = firstStreamedDate;
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

        public Builder withImageUrl(Image imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public AdvancedRankedTrack build() {
            return new AdvancedRankedTrack(ranking, trackName, artistName, albumName, trackUri, firstStreamedDate, lastStreamedDate, totalMinutesPlayed, totalMsPlayed, totalStreams, imageUrl);
        }
    }
}
