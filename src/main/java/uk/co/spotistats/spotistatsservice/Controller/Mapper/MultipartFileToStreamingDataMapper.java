package uk.co.spotistats.spotistatsservice.Controller.Mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.someStreamingData;

@Component
public class MultipartFileToStreamingDataMapper {

    private final ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(MultipartFileToStreamingDataMapper.class);

    public MultipartFileToStreamingDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Result<StreamingData, Error> map(MultipartFile file) {
        try {
            JsonNode streamingDataJson = objectMapper.readTree(new String(file.getBytes()));
            List<StreamData> streamData = StreamSupport.stream(streamingDataJson.spliterator(), false).map(this::mapStreamData).toList();
            return new Result.Success<>(someStreamingData()
                    .withStreamData(streamData)
                    .withFirstStreamDateTime(streamData.getFirst().streamDateTime())
                    .withLastStreamDateTime(streamData.getLast().streamDateTime())
                    .withSize(streamData.size())
                    .build());
        } catch (IOException ioException) {
            LOG.error("Exception accessing bytes of supplied file", ioException);
            return new Result.Failure<>(Error.failedToParseData(file.getName(), "failed to read streaming data"));
        }
    }

    private StreamData mapStreamData(JsonNode streamData) {
        return objectMapper.convertValue(streamData, StreamData.class);
    }
}
