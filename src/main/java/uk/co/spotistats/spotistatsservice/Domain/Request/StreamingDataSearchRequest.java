package uk.co.spotistats.spotistatsservice.Domain.Request;

import java.time.LocalDate;

import static uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

public record StreamingDataSearchRequest(
        String username,
        LocalDate start,
        LocalDate end,
        LocalDate on,
        String country,
        String uri,
        String trackName,
        String artist,
        String album,
        String platform,
        String orderBy,
        Integer limit
) {

    public static final class Builder {
        private String username;
        private LocalDate start;
        private LocalDate end;
        private LocalDate on;
        private String country;
        private String uri;
        private String trackName;
        private String artist;
        private String album;
        private String platform;
        private String orderBy;
        private Integer limit;

        private Builder() {
        }

        public static Builder aStreamingDataSearchRequest() {
            return new Builder();
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withStart(LocalDate start) {
            this.start = start;
            return this;
        }

        public Builder withEnd(LocalDate end) {
            this.end = end;
            return this;
        }

        public Builder withOn(LocalDate on) {
            this.on = on;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withTrackName(String trackName) {
            this.trackName = trackName;
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

        public Builder withOrderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public StreamingDataSearchRequest build() {
            return new StreamingDataSearchRequest(username, start, end, on, country, uri, trackName, artist, album, platform, orderBy, limit);
        }
    }

    public StreamingDataSearchRequest.Builder cloneBuilder(){
        return aStreamingDataSearchRequest()
                .withUsername(username)
                .withStart(start)
                .withEnd(end)
                .withOn(on)
                .withCountry(country)
                .withUri(uri)
                .withTrackName(trackName)
                .withArtist(artist)
                .withAlbum(album)
                .withPlatform(platform)
                .withOrderBy(orderBy)
                .withLimit(limit);
    }
}
