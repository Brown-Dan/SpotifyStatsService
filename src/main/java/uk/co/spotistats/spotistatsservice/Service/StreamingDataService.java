package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataInsertResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.UUID;

import static uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataInsertResult.Builder.aStreamingDataInsertResult;

@Service
public class StreamingDataService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    private final StreamingDataRepository streamingDataRepository;

    public StreamingDataService(StreamingDataRepository streamingDataRepository) {
        this.streamingDataRepository = streamingDataRepository;
    }

    public Result<StreamingDataInsertResult, Error> insert(StreamingData streamingData) {
        UUID uuid = streamingDataRepository.insertStreamingData(streamingData, "danBrown05");
        streamingData.streamData().forEach(streamData -> streamingDataRepository.insertStreamData(streamData, uuid));
        return new Result.Success<>(aStreamingDataInsertResult()
                .withStreamCount(streamingData.streamCount())
                .withFirstStreamDateTime(streamingData.firstStreamDateTime())
                .withLastStreamDateTime(streamingData.lastStreamDateTime()).build());
    }
}
