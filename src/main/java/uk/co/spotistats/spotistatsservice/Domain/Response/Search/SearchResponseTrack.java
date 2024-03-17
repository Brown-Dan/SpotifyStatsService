package uk.co.spotistats.spotistatsservice.Domain.Response.Search;

import java.time.LocalDateTime;

public record SearchResponseTrack(LocalDateTime streamDateTime, String trackName, String artistName, String albumName, String trackUri, long totalMsPlayed, String platform, String country) {

    public static final class Builder {
        private LocalDateTime streamDateTime;
        private String trackName;
        private String artistName;
        private String albumName;
        private String trackUri;
        private long totalMsPlayed;
        private String platform;
        private String country;

        private Builder() {
        }

        public static Builder aSearchResponseTrack() {
            return new Builder();
        }

        public Builder withStreamDateTime(LocalDateTime streamDateTime) {
            this.streamDateTime = streamDateTime;
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

        public Builder withTotalMsPlayed(long totalMsPlayed) {
            this.totalMsPlayed = totalMsPlayed;
            return this;
        }

        public Builder withPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public SearchResponseTrack build() {
            return new SearchResponseTrack(streamDateTime, trackName, artistName, albumName, trackUri, totalMsPlayed, platform, country);
        }
    }
}
