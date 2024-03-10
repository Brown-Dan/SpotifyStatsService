package uk.co.spotistats.spotistatsservice.Controller.Mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataUploadService;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import static uk.co.spotistats.spotistatsservice.Domain.StreamingData.Builder.aStreamingData;

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
            return new Result.Success<>(aStreamingData()
                    .withStreamData(streamData)
                    .withFirstStreamDateTime(streamData.getFirst().streamDateTime())
                    .withLastStreamDateTime(streamData.getLast().streamDateTime())
                    .withTotalStreams(streamData.size())
                    .build());
        } catch (IOException ioException) {
            LOG.error("Exception accessing bytes of supplied file", ioException);
            return new Result.Failure<>(new Error("Failed to read streaming data - %s".formatted(file.getName())));
        }
    }

    private StreamData mapStreamData(JsonNode streamData) {
        return objectMapper.convertValue(streamData, StreamData.class);
    }
}
