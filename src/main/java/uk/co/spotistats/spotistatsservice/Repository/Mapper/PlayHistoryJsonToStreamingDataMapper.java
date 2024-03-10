package uk.co.spotistats.spotistatsservice.Repository.Mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import static uk.co.spotistats.spotistatsservice.Domain.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.StreamingData.Builder.aStreamingData;

@Component
public class PlayHistoryJsonToStreamingDataMapper {

    private final ObjectMapper objectMapper;

    public PlayHistoryJsonToStreamingDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Result<StreamingData, Error> map(JSONObject playHistory) {
        try {
            JsonNode responseAsJsonNode = objectMapper.readTree(playHistory.toJSONString()).get("items");
            List<StreamData> streamData = StreamSupport.stream(responseAsJsonNode.spliterator(), false).map(item -> mapStreamData(item.get("track"))
                    .withStreamDateTime(LocalDateTime.parse(item.get("played_at").asText().replace("Z", ""))).build()).toList();

            return new Result.Success<>(aStreamingData()
                    .withStreamData(streamData)
                    .withTotalStreams(streamData.size())
                    .withFirstStreamDateTime(streamData.getLast().streamDateTime())
                    .withLastStreamDateTime(streamData.getFirst().streamDateTime()).build());
        } catch (Exception e) {
            return new Result.Failure<>(new Error("Failed to parse recent streaming data"));
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
