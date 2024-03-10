package uk.co.spotistats.spotistatsservice.Domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record StreamData(
        @JsonProperty("ts") LocalDateTime streamDateTime,
        @JsonProperty("conn_country") String country,
        @JsonProperty("ms_played") long timeStreamed,
        @JsonProperty("spotify_track_uri") String trackUri,
        @JsonProperty("master_metadata_track_name") String name,
        @JsonProperty("master_metadata_album_artist_name") String artist,
        @JsonProperty("master_metadata_album_album_name") String album,
        @JsonProperty("platform") String platform) {

    public static final class Builder {
        private LocalDateTime streamDateTime;
        private String country;
        private long timeStreamed;
        private String trackUri;
        private String name;
        private String artist;
        private String album;
        private String platform;

        private Builder() {
        }

        public static Builder aStreamData() {
            return new Builder();
        }

        public Builder withStreamDateTime(LocalDateTime streamDateTime) {
            this.streamDateTime = streamDateTime;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withTimeStreamed(long timeStreamed) {
            this.timeStreamed = timeStreamed;
            return this;
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

        public Builder withPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public StreamData build() {
            return new StreamData(streamDateTime, country, timeStreamed, trackUri, name, artist, album, platform);
        }
    }
}