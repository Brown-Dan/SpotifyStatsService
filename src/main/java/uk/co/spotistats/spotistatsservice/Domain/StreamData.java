package uk.co.spotistats.spotistatsservice.Domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record StreamData(
        @JsonProperty("ts") LocalDateTime streamDateTime,
        @JsonProperty("conn_country") String country,
        @JsonProperty("ms_played") long timeStreamed,
        @JsonProperty("spotify_track_uri") String trackUri,
        @JsonProperty("master_metadata_track_name") String name,
        @JsonProperty("master_metadata_album_artist_name") String artist,
        @JsonProperty("master_metadata_album_album_name") String album,
        @JsonProperty("platform") String platform) {
}