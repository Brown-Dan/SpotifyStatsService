package uk.co.spotistats.spotistatsservice.Domain.Request;

import java.util.List;

import static uk.co.spotistats.spotistatsservice.Domain.Request.Playlist.Builder.aPlaylist;

public record Playlist(List<String> tracks, String owner, String name, String uri, String id) {

    public static final class Builder {
        private List<String> tracks;
        private String owner;
        private String name;
        private String uri;
        private String id;

        private Builder() {
        }

        public static Builder aPlaylist() {
            return new Builder();
        }

        public Builder withTracks(List<String> tracks) {
            this.tracks = tracks;
            return this;
        }

        public Builder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Playlist build() {
            return new Playlist(tracks, owner, name, uri, id);
        }
    }

    public Playlist.Builder cloneBuilder() {
        return aPlaylist()
                .withOwner(owner)
                .withId(id)
                .withName(name)
                .withUri(uri)
                .withTracks(tracks);
    }
}
