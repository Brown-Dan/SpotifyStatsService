package uk.co.spotistats.spotistatsservice.Domain.Request.Search;

import java.time.LocalDate;
import java.time.LocalTime;

import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

public record StreamingDataSearchRequest(
        String username,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate onDate,
        String country,
        String uri,
        String trackName,
        String artist,
        String album,
        String platform,
        String orderBy,
        Integer limit,
        LocalTime startTime,
        LocalTime endTime
) {

    public static final class Builder {

        private String username;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate onDate;
        private String country;
        private String uri;
        private String trackName;
        private String artist;
        private String album;
        private String platform;
        private String orderBy;
        private Integer limit;
        private LocalTime startTime;
        private LocalTime endTime;

        private Builder() {
        }

        public static Builder aStreamingDataSearchRequest() {
            return new Builder();
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder withOnDate(LocalDate onDate) {
            this.onDate = onDate;
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

        public Builder withStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withEndTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public StreamingDataSearchRequest build() {
            return new StreamingDataSearchRequest(username, startDate, endDate, onDate, country, uri, trackName, artist, album, platform, orderBy, limit, startTime, endTime);
        }
    }

    public StreamingDataSearchRequest.Builder cloneBuilder() {
        return aStreamingDataSearchRequest()
                .withUsername(username)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withOnDate(onDate)
                .withStartTime(startTime)
                .withEndTime(endTime)
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