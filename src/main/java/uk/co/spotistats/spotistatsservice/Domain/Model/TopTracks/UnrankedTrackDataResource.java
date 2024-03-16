package uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks;

public record UnrankedTrackDataResource(String trackUri, String name, String artist, String album, long lengthMs, Integer rank) {

    public static final class Builder {
        private String trackUri;
        private String name;
        private String artist;
        private String album;
        private long lengthMs;
        private Integer rank;

        private Builder() {
        }

        public static Builder anUnrankedTrackDataResource() {
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

        public Builder withRank(Integer rank) {
            this.rank = rank;
            return this;
        }

        public UnrankedTrackDataResource build() {
            return new UnrankedTrackDataResource(trackUri, name, artist, album, lengthMs, rank);
        }
    }
}
