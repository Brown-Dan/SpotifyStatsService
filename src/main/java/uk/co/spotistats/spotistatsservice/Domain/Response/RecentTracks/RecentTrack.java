package uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks;

import java.time.LocalDateTime;

public record RecentTrack(String trackUri, String name, String artist, String album, long lengthMs, LocalDateTime streamDateTime) {

    public static final class Builder {
        private String trackUri;
        private String name;
        private String artist;
        private String album;
        private long lengthMs;
        private LocalDateTime streamDateTime;

        private Builder() {
        }

        public static Builder aRecentTrack() {
            return new Builder();
        }

        public Builder withTrackUri(String trackUri) {
            this.trackUri = trackUri;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder withAlbum(String album) {
            this.album = album;
            return this;
        }

        public Builder withLengthMs(long lengthMs) {
            this.lengthMs = lengthMs;
            return this;
        }

        public Builder withStreamDateTime(LocalDateTime streamDateTime) {
            this.streamDateTime = streamDateTime;
            return this;
        }

        public RecentTrack build() {
            return new RecentTrack(trackUri, name, artist, album, lengthMs, streamDateTime);
        }
    }
}
