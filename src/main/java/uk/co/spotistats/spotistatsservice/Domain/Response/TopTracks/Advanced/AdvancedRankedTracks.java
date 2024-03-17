package uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Advanced;

import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracks;

import java.util.List;

public record AdvancedRankedTracks(List<AdvancedRankedTrack> tracks, int totalResults,
                                   int page, boolean createdPlaylist, String playlistId) implements TopTracks {

    public static final class Builder {
        private List<AdvancedRankedTrack> tracks;
        private int totalResults;
        private int page;
        private boolean createdPlaylist;
        private String playlistId;

        private Builder() {
        }

        public static Builder someAdvancedRankedTracks() {
            return new Builder();
        }

        public Builder withTracks(List<AdvancedRankedTrack> tracks) {
            this.tracks = tracks;
            return this;
        }

        public Builder withTotalResults(int totalResults) {
            this.totalResults = totalResults;
            return this;
        }

        public Builder withPage(int page) {
            this.page = page;
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

        public AdvancedRankedTracks build() {
            return new AdvancedRankedTracks(tracks, totalResults, page, createdPlaylist, playlistId);
        }
    }
}
