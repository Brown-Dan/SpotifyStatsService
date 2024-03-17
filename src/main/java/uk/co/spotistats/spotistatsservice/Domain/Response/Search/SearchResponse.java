package uk.co.spotistats.spotistatsservice.Domain.Response.Search;

import java.time.LocalDateTime;
import java.util.List;

import static uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse.Builder.aSearchResponse;

public record SearchResponse(List<SearchResponseTrack> tracks, int size, LocalDateTime firstStreamDate, LocalDateTime lastStreamDate, boolean createdPlaylist, String playlistId, int totalStreamTimeMinutes) {

    public static final class Builder {
        private List<SearchResponseTrack> tracks;
        private int size;
        private LocalDateTime firstStreamDate;
        private LocalDateTime lastStreamDate;
        private boolean createdPlaylist;
        private String playlistId;
        private int totalStreamTimeMinutes;

        private Builder() {
        }

        public static Builder aSearchResponse() {
            return new Builder();
        }

        public Builder withTracks(List<SearchResponseTrack> tracks) {
            this.tracks = tracks;
            return this;
        }

        public Builder withSize(int size) {
            this.size = size;
            return this;
        }

        public Builder withFirstStreamDate(LocalDateTime firstStreamDate) {
            this.firstStreamDate = firstStreamDate;
            return this;
        }

        public Builder withLastStreamDate(LocalDateTime lastStreamDate) {
            this.lastStreamDate = lastStreamDate;
            return this;
        }

        public Builder withCreatedPlaylist(boolean createdPlaylist) {
            this.createdPlaylist = createdPlaylist;
            return this;
        }

        public Builder withPlaylistId(String playlistId) {
            this.playlistId = playlistId;
            return this;
        }

        public Builder withTotalStreamTimeMinutes(int totalStreamTimeMinutes) {
            this.totalStreamTimeMinutes = totalStreamTimeMinutes;
            return this;
        }

        public SearchResponse build() {
            return new SearchResponse(tracks, size, firstStreamDate, lastStreamDate, createdPlaylist, playlistId, totalStreamTimeMinutes);
        }
    }

    public SearchResponse.Builder cloneBuilder(){
        return aSearchResponse()
                .withTracks(tracks)
                .withSize(size)
                .withFirstStreamDate(firstStreamDate)
                .withLastStreamDate(lastStreamDate)
                .withCreatedPlaylist(createdPlaylist)
                .withPlaylistId(playlistId)
                .withTotalStreamTimeMinutes(totalStreamTimeMinutes);
    }
}
