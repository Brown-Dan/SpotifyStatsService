package uk.co.spotistats.spotistatsservice.Repository.Mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.Playlist;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyRefreshTokenResponse;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.aStreamingData;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Playlist.Builder.aPlaylist;

@Component
public class SpotifyResponseMapper {

    private final ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public SpotifyResponseMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public StreamingData fromRecentStreamingData(JSONObject json) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(json.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> toStreamData(item.get("track"))
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();
            return aStreamingData()
                    .withStreamData(streamData)
                    .withSize(streamData.size())
                    .withFirstStreamDateTime(streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.getFirst().streamDateTime()).build();
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            throw new RuntimeException();
        }
    }

    public StreamingData fromTopTracks(JSONObject json) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(json.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> toStreamData(item)
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();
            return aStreamingData()
                    .withStreamData(streamData)
                    .withSize(streamData.size())
                    .withFirstStreamDateTime(streamData.isEmpty() ? null : streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.isEmpty() ? null : streamData.getFirst().streamDateTime()).build();
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            throw new RuntimeException();
        }
    }

    public SpotifyRefreshTokenResponse toRefreshTokenResponse(JSONObject resource) {
        return objectMapper.convertValue(resource, SpotifyRefreshTokenResponse.class);
    }

    public SpotifyAuthData toSpotifyAuthData(JSONObject jsonObject) {
        return objectMapper.convertValue(jsonObject, SpotifyAuthData.class);
    }

    public String toUserProfile(JSONObject jsonObject) {
        return jsonObject.get("id").toString();
    }

    public Playlist toPlaylist(JSONObject json) {
        return aPlaylist()
                .withId(json.get("id").toString())
                .withUri(json.get("uri").toString())
                .withName(json.get("name").toString())
                .withOwner(json.getJSONObject("owner").get("id").toString())
                .build();
    }

    private StreamData.Builder toStreamData(JsonNode track) {
        return aStreamData()
                .withAlbum(track.get("album").get("name").asText())
                .withArtist(track.get("artists").get(0).get("name").asText())
                .withName(track.get("name").asText())
                .withTrackUri(track.get("uri").asText())
                .withTimeStreamed(track.get("duration_ms").asLong());
    }
}
