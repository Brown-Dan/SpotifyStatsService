package uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists;

import uk.co.spotistats.spotistatsservice.Domain.Model.Image;

import java.time.LocalDateTime;
import java.util.List;

public record AdvancedTopArtist(Integer ranking, String name, String spotifyUri, Integer popularity,
                                List<String> genres, Integer totalMinutesStreamed, Long totalMsStreamed,
                                LocalDateTime firstStreamedDate, LocalDateTime lastStreamedDate, Integer totalStreams, Image image) {


    public static final class Builder {
        private Integer ranking;
        private String name;
        private String spotifyUri;
        private Integer popularity;
        private List<String> genres;
        private Integer totalMinutesStreamed;
        private Long totalMsStreamed;
        private LocalDateTime firstStreamedDate;
        private LocalDateTime lastStreamedDate;
        private Integer totalStreams;
        private Image image;

        private Builder() {
        }

        public static Builder anAdvancedTopArtist() {
            return new Builder();
        }

        public Builder withRanking(Integer ranking) {
            this.ranking = ranking;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSpotifyUri(String spotifyUri) {
            this.spotifyUri = spotifyUri;
            return this;
        }

        public Builder withPopularity(Integer popularity) {
            this.popularity = popularity;
            return this;
        }

        public Builder withGenres(List<String> genres) {
            this.genres = genres;
            return this;
        }

        public Builder withTotalMinutesStreamed(Integer totalMinutesStreamed) {
            this.totalMinutesStreamed = totalMinutesStreamed;
            return this;
        }

        public Builder withTotalMsStreamed(Long totalMsStreamed) {
            this.totalMsStreamed = totalMsStreamed;
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

        public Builder withTotalStreams(Integer totalStreams) {
            this.totalStreams = totalStreams;
            return this;
        }

        public Builder withImage(Image image) {
            this.image = image;
            return this;
        }

        public AdvancedTopArtist build() {
            return new AdvancedTopArtist(ranking, name, spotifyUri, popularity, genres, totalMinutesStreamed, totalMsStreamed, firstStreamedDate, lastStreamedDate, totalStreams, image);
        }
    }
}
