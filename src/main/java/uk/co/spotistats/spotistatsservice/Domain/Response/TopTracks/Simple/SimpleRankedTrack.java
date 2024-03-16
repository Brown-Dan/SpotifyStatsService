package uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Simple;

public record SimpleRankedTrack(Integer rank, String trackUri, String name, String artist, String album, long lengthMs) {

    public static final class Builder {
        private String trackUri;
        private String name;
        private String artist;
        private String album;
        private long lengthMs;
        private Integer rank;

        private Builder() {
        }

        public static Builder aSimpleRankedTrack() {
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

        public SimpleRankedTrack build() {
            return new SimpleRankedTrack(rank, trackUri, name, artist, album, lengthMs);
        }
    }
}
