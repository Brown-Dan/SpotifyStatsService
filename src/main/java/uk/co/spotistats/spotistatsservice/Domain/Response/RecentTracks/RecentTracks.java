package uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks;

import java.time.LocalDateTime;
import java.util.List;

import static uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks.Builder.someRecentTracks;

public record RecentTracks(List<RecentTrack> tracks, Integer size,
                           LocalDateTime firstStreamDateTime,
                           LocalDateTime lastStreamDateTime, Boolean createdPlaylist, String playlistId, Integer totalStreamTimeMinutes) {

    public static final class Builder {
        private List<RecentTrack> tracks;
        private Integer size;
        private LocalDateTime firstStreamDateTime;
        private LocalDateTime lastStreamDateTime;
        private Boolean createdPlaylist;
        private String playlistId;
        private Integer totalStreamTimeMinutes;

        private Builder() {
        }

        public static Builder someRecentTracks() {
            return new Builder();
        }

        public Builder withTracks(List<RecentTrack> tracks) {
            this.tracks = tracks;
            return this;
        }

        public Builder withSize(Integer size) {
            this.size = size;
            return this;
        }

        public Builder withFirstStreamDateTime(LocalDateTime firstStreamDateTime) {
            this.firstStreamDateTime = firstStreamDateTime;
            return this;
        }

        public Builder withLastStreamDateTime(LocalDateTime lastStreamDateTime) {
            this.lastStreamDateTime = lastStreamDateTime;
            return this;
        }

        public Builder withCreatedPlaylist(Boolean createdPlaylist) {
            this.createdPlaylist = createdPlaylist;
            return this;
        }

        public Builder withPlaylistId(String playlistId) {
            this.playlistId = playlistId;
            return this;
        }

        public Builder withTotalStreamTimeMinutes(Integer totalStreamTimeMinutes) {
            this.totalStreamTimeMinutes = totalStreamTimeMinutes;
            return this;
        }

        public RecentTracks build() {
            return new RecentTracks(tracks, size, firstStreamDateTime, lastStreamDateTime, createdPlaylist, playlistId, totalStreamTimeMinutes);
        }
    }


    public RecentTracks addPlaylist(String playlistId){
        return someRecentTracks()
                .withTotalStreamTimeMinutes(totalStreamTimeMinutes)
                .withCreatedPlaylist(true)
                .withPlaylistId(playlistId)
                .withTracks(tracks)
                .withSize(size)
                .withLastStreamDateTime(lastStreamDateTime)
                .withFirstStreamDateTime(firstStreamDateTime)
                .build();
    }
}
