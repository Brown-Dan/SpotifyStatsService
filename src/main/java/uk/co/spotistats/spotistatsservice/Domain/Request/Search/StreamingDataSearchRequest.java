package uk.co.spotistats.spotistatsservice.Domain.Request.Search;

import java.time.LocalDate;
import java.time.LocalTime;

import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

public record StreamingDataSearchRequest(
        String userId,
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
        LocalTime endTime,
        String dayOfTheWeek,
        String month,
        String year,
        Boolean createPlaylist
) {
    public static final class Builder {
        private String userId;
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
        private String dayOfTheWeek;
        private String month;
        private String year;
        private Boolean createPlaylist;

        private Builder() {
        }

        public static Builder aStreamingDataSearchRequest() {
            return new Builder();
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
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

        public Builder withDayOfTheWeek(String dayOfTheWeek) {
            this.dayOfTheWeek = dayOfTheWeek;
            return this;
        }

        public Builder withMonth(String month) {
            this.month = month;
            return this;
        }

        public Builder withYear(String year) {
            this.year = year;
            return this;
        }

        public Builder withCreatePlaylist(Boolean createPlaylist) {
            this.createPlaylist = createPlaylist;
            return this;
        }

        public StreamingDataSearchRequest build() {
            return new StreamingDataSearchRequest(userId, startDate, endDate, onDate, country, uri, trackName, artist, album, platform, orderBy, limit, startTime, endTime, dayOfTheWeek, month, year, createPlaylist);
        }
    }

    @Override
    public String toString() {
        return "StreamingDataSearchRequest{" +
               "userId='" + userId + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               ", onDate=" + onDate +
               ", country='" + country + '\'' +
               ", uri='" + uri + '\'' +
               ", trackName='" + trackName + '\'' +
               ", artist='" + artist + '\'' +
               ", album='" + album + '\'' +
               ", platform='" + platform + '\'' +
               ", orderBy='" + orderBy + '\'' +
               ", limit=" + limit +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", dayOfTheWeek=" + dayOfTheWeek +
               ", month=" + month +
               ", year=" + year +
               ", createPlaylist=" + createPlaylist +
               '}';
    }

    public StreamingDataSearchRequest.Builder cloneBuilder() {
        return aStreamingDataSearchRequest()
                .withUserId(userId)
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
                .withCreatePlaylist(createPlaylist)
                .withDayOfTheWeek(dayOfTheWeek)
                .withMonth(month)
                .withYear(year)
                .withLimit(limit);
    }
}