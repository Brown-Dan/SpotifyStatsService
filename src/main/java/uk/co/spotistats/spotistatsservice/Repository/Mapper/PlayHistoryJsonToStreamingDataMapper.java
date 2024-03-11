package uk.co.spotistats.spotistatsservice.Repository.Mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static uk.co.spotistats.spotistatsservice.Domain.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.StreamingData.Builder.aStreamingData;

@Component
public class PlayHistoryJsonToStreamingDataMapper {

    private final ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public PlayHistoryJsonToStreamingDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Result<StreamingData, Error> map(JSONObject playHistory) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(playHistory.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> mapStreamData(item.get("track"))
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();

            return new Result.Success<>(aStreamingData()
                    .withStreamData(streamData)
                    .withTotalStreams(streamData.size())
                    .withFirstStreamDateTime(streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.getFirst().streamDateTime()).build());
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            return new Result.Failure<>(new Error("Failed to parse streaming data"));
        }
    }

    public Result<StreamingData, Error> mapFromTopStreams(JSONObject playHistory) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(playHistory.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> mapStreamData(item)
                    .withStreamDateTime(Optional.ofNullable(item.get("played_at")).map(JsonNode::asText).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime).orElse(null)).build()).toList();

            return new Result.Success<>(aStreamingData()
                    .withStreamData(streamData)
                    .withTotalStreams(streamData.size())
                    .withFirstStreamDateTime(streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.getFirst().streamDateTime()).build());
        } catch (Exception e) {
            LOG.error("Failed to parse streaming data");
            return new Result.Failure<>(new Error("Failed to parse streaming data"));
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
