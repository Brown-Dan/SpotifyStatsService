package uk.co.spotistats.spotistatsservice.Domain.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponseTrack;

import java.time.LocalDateTime;

import static uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack.Builder.anAdvancedTrack;

public record AdvancedTrack(
        @JsonProperty String trackName,
        @JsonProperty String artistName,
        @JsonProperty String albumName,
        @JsonProperty String trackUri,
        @JsonProperty LocalDateTime firstStreamDate,
        @JsonProperty LocalDateTime lastStreamedDate,
        @JsonProperty int totalMinutesPlayed,
        @JsonProperty long totalMsPlayed,
        @JsonProperty int totalStreams) {


    public static AdvancedTrack fromSearchResponse(SearchResponse searchResponse) {
        if (searchResponse.tracks().isEmpty()){
            return AdvancedTrack.Builder.anAdvancedTrack().build();
        }
        long totalMsPlayed = searchResponse.tracks().stream().mapToLong(SearchResponseTrack::totalMsPlayed).sum();
        SearchResponseTrack track = searchResponse.tracks().getFirst();
        return anAdvancedTrack()
                .withAlbumName(track.albumName())
                .withTrackName(track.trackName())
                .withArtistName(track.artistName())
                .withTrackUri(track.trackUri())
                .withFirstStreamDate(searchResponse.firstStreamDate())
                .withLastStreamedDate(searchResponse.lastStreamDate())
                .withTotalMsPlayed(totalMsPlayed)
                .withTotalStreams(searchResponse.tracks().size())
                .withTotalMinutesPlayed(searchResponse.totalStreamTimeMinutes())
                .build();
    }

    public static final class Builder {
        private String trackName;
        private String artistName;
        private String albumName;
        private String trackUri;
        private LocalDateTime firstStreamDate;
        private LocalDateTime lastStreamedDate;
        private int totalMinutesPlayed;
        private long totalMsPlayed;
        private int totalStreams;

        private Builder() {
        }

        public static Builder anAdvancedTrack() {
            return new Builder();
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

        public Builder withFirstStreamDate(LocalDateTime firstStreamDate) {
            this.firstStreamDate = firstStreamDate;
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

        public Builder withTotalMsPlayed(long totalMsPlayed) {
            this.totalMsPlayed = totalMsPlayed;
            return this;
        }

        public Builder withTotalStreams(int totalStreams) {
            this.totalStreams = totalStreams;
            return this;
        }

        public AdvancedTrack build() {
            return new AdvancedTrack(trackName, artistName, albumName, trackUri, firstStreamDate, lastStreamedDate, totalMinutesPlayed, totalMsPlayed, totalStreams);
        }
    }
}
