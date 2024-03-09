package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.StreamingData.Builder.aStreamingData;

@Service
public class StreamingDataService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    private final StreamingDataRepository streamingDataRepository;

    public StreamingDataService(StreamingDataRepository streamingDataRepository) {
        this.streamingDataRepository = streamingDataRepository;
    }

    public Result<StreamingDataUpsertResult, Error> upsert(StreamingData streamingData, String username) {
        StreamingDataUpsertResult streamingDataUpsertResult;

        Optional<StreamingData> existingStreamingData = streamingDataRepository.getStreamingDataByUsername(username);

        if (existingStreamingData.isPresent()) {
            Optional<StreamingDataUpsertResult> updateResult = streamingDataRepository
                    .updateStreamingData(combineStreamingData(existingStreamingData.get(), streamingData), username);
            if (updateResult.isEmpty()) {
                return new Result.Failure<>(new Error("Failure updating streaming data - %s for username - %s ".formatted(streamingData, username)));
            }
            streamingDataUpsertResult = updateResult.get();
            LOG.info("Updated streaming data for user - {}", username);
        } else {
            Optional<StreamingDataUpsertResult> insertResult = streamingDataRepository
                    .insertStreamingData(streamingData, username);
            if (insertResult.isEmpty()) {
                return new Result.Failure<>(new Error("Failure inserting streaming data - %s for username - %s ".formatted(streamingData, username)));
            }
            streamingDataUpsertResult = insertResult.get();
            LOG.info("Inserted streaming data for user - {}", username);
        }
        streamingData.streamData().forEach(streamData -> streamingDataRepository.insertStreamData(streamData, username));
        return new Result.Success<>(streamingDataUpsertResult);
    }

    private StreamingData combineStreamingData(StreamingData originalData, StreamingData newData) {
        LocalDateTime firstStreamDateTime = originalData.firstStreamDateTime().isBefore(newData.firstStreamDateTime())
                ? originalData.firstStreamDateTime() : newData.firstStreamDateTime();
        LocalDateTime lastStreamDateTime = originalData.lastStreamDateTime().isAfter(newData.lastStreamDateTime())
                ? originalData.lastStreamDateTime() : newData.lastStreamDateTime();

        return aStreamingData()
                .withTotalStreams(originalData.streamCount() + newData.streamCount())
                .withFirstStreamDateTime(firstStreamDateTime)
                .withLastStreamDateTime(lastStreamDateTime)
                .withStreamData(newData.streamData())
                .build();
    }
}
