package uk.co.spotistats.spotistatsservice.Repository.Mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.aStreamingData;

@Component
public class SpotifyResponseJsonToStreamingDataMapper {

    private final ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public SpotifyResponseJsonToStreamingDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Result<StreamingData, Errors> mapFromRecentStreamsJson(JSONObject json) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(json.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> mapStreamData(item.get("track"))
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();

            return new Result.Success<>(aStreamingData()
                    .withStreamData(streamData)
                    .withTotalStreams(streamData.size())
                    .withFirstStreamDateTime(streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.getFirst().streamDateTime()).build());
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            return new Result.Failure<>(Errors.fromError(Error.failedToParseData("recentStreams", "failed to read streaming data")));
        }
    }

    public Result<StreamingData, Errors> mapFromTopStreamsJson(JSONObject json) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(json.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> mapStreamData(item)
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();

            return new Result.Success<>(aStreamingData()
                    .withStreamData(streamData)
                    .withTotalStreams(streamData.size())
                    .withFirstStreamDateTime(streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.getFirst().streamDateTime()).build());
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            return new Result.Failure<>(Errors.fromError(Error.failedToParseData("topStreams", "failed to read streaming data")));
        }
    }

    private StreamData.Builder mapStreamData(JsonNode track) {
        return aStreamData()
                .withAlbum(track.get("album").get("name").asText())
                .withArtist(track.get("artists").get(0).get("name").asText())
                .withName(track.get("name").asText())
                .withTrackUri(track.get("uri").asText())
                .withTimeStreamed(track.get("duration_ms").asLong());
    }
}
