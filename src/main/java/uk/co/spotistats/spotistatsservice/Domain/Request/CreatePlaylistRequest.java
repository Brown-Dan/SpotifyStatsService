package uk.co.spotistats.spotistatsservice.Domain.Request;

import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;

import java.util.List;

public record CreatePlaylistRequest(SpotifyAuthData authData, String name, String description, List<String> trackUris) {

    public static final class Builder {
        private SpotifyAuthData spotifyAuthData;
        private String name;
        private String description;
        private List<String> trackUris;

        private Builder() {
        }

        public static Builder aCreatePlaylistRequest() {
            return new Builder();
        }

        public Builder withSpotifyAuthData(SpotifyAuthData spotifyAuthData) {
            this.spotifyAuthData = spotifyAuthData;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withTrackUris(List<String> trackUris) {
            this.trackUris = trackUris;
            return this;
        }

        public CreatePlaylistRequest build() {
            return new CreatePlaylistRequest(spotifyAuthData, name, description, trackUris);
        }
    }
}
