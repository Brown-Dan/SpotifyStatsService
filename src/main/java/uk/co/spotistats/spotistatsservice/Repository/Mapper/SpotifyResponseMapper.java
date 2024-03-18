package uk.co.spotistats.spotistatsservice.Repository.Mapper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Model.Image;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.User;
import uk.co.spotistats.spotistatsservice.Domain.Request.Playlist;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.SimpleTopArtist;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.SimpleTopArtists;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyRefreshTokenResponse;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static uk.co.spotistats.spotistatsservice.Domain.Model.Image.Builder.anImage;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.someStreamingData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.User.Builder.anUser;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Playlist.Builder.aPlaylist;
import static uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTrack.Builder.aRecentTrack;
import static uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks.Builder.someRecentTracks;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.SimpleTopArtist.Builder.aSimpleTopArtist;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.SimpleTopArtists.Builder.aSimpleTopArtists;

@Component
public class SpotifyResponseMapper {

    private final ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public SpotifyResponseMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RecentTracks toRecentTracks(JSONObject json) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(json.toJSONString()).get("items");
            List<RecentTrack> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> toRecentTrack(item.get("track"))
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();
            return someRecentTracks()
                    .withCreatedPlaylist(false)
                    .withTotalStreamTimeMinutes(((int) streamData.stream().mapToLong(RecentTrack::lengthMs).sum() / 1000) / 60)
                    .withTracks(streamData)
                    .withSize(streamData.size())
                    .withFirstStreamDateTime(streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.getFirst().streamDateTime()).build();
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            throw new RuntimeException();
        }
    }

    private RecentTrack.Builder toRecentTrack(JsonNode track) {
        return aRecentTrack()
                .withAlbum(track.get("album").get("name").asText())
                .withArtist(track.get("artists").get(0).get("name").asText())
                .withName(track.get("name").asText())
                .withTrackUri(track.get("uri").asText())
                .withLengthMs(track.get("duration_ms").asLong());
    }

    public StreamingData fromTopTracks(JSONObject json) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(json.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> toStreamData(item)
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();
            return someStreamingData()
                    .withStreamData(streamData)
                    .withSize(streamData.size())
                    .withFirstStreamDateTime(streamData.isEmpty() ? null : streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.isEmpty() ? null : streamData.getFirst().streamDateTime()).build();
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            throw new RuntimeException();
        }
    }

    public SimpleTopArtists fromTopArtists(JSONObject json, int page) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(json.toJSONString()).get("items");
            List<SimpleTopArtist> artists = StreamSupport.stream(responseAsJsonNode.spliterator(), false)
                    .map(this::mapToArtist).toList();
            return aSimpleTopArtists()
                    .withArtists(artists)
                    .withTotalResults(artists.size())
                    .withPage(page)
                    .build();
        } catch (Exception e) {
            LOG.error("Failed to parse spotify api response");
            throw new RuntimeException();
        }
    }

    private SimpleTopArtist mapToArtist(JsonNode artist){
        List<String> genres = new ArrayList<>();
        JsonNode genresNode = artist.get("genres");
        if (genresNode.isArray()) {
            for (JsonNode genreNode : genresNode) {
                genres.add(genreNode.asText());
            }
        }
        JsonNode images = artist.get("images");
        List<JSONObject> imagesList = new ArrayList<>();
        for (JsonNode image : images){
            imagesList.add(JSON.parseObject(image.toPrettyString()));
        }

        return aSimpleTopArtist()
                .withName(artist.get("name").asText())
                .withGenres(genres)
                .withImage(mapImage(imagesList.isEmpty() ? null : imagesList.getFirst()))
                .withPopularity(artist.get("popularity").asInt())
                .withSpotifyUri(artist.get("uri").asText())
                .build();
    }

    public SpotifyRefreshTokenResponse toRefreshTokenResponse(JSONObject resource) {
        return objectMapper.convertValue(resource, SpotifyRefreshTokenResponse.class);
    }

    public SpotifyAuthData toSpotifyAuthData(JSONObject jsonObject) {
        return objectMapper.convertValue(jsonObject, SpotifyAuthData.class);
    }

    public User toUserProfile(JSONObject jsonObject) {
        return anUser()
                .withId(jsonObject.get("id").toString())
                .withUrl(jsonObject.get("uri").toString())
                .withImage(mapImage(jsonObject.getJSONArray("images").isEmpty() ? null :  jsonObject.getJSONArray("images").getJSONObject(0)))
                .build();
    };

    public Image mapImage(JSONObject image){
        if (image == null){
            return null;
        }
        return anImage()
                .withUri(image.getString("url"))
                .withHeight(image.getInteger("height"))
                .withWidth(image.getInteger("width"))
                .build();
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
        JsonNode images = track.get("album").get("images");
        List<JSONObject> imagesList = new ArrayList<>();
        for (JsonNode image : images){
            imagesList.add(JSON.parseObject(image.toPrettyString()));
        }

        return aStreamData()
                .withAlbum(track.get("album").get("name").asText())
                .withArtist(track.get("artists").get(0).get("name").asText())
                .withName(track.get("name").asText())
                .withImage(mapImage(imagesList.isEmpty() ? null : imagesList.getFirst()))
                .withTrackUri(track.get("uri").asText())
                .withTimeStreamed(track.get("duration_ms").asLong());
    }
}
