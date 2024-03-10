package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataUploadRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.StreamingData.Builder.aStreamingData;

@Service
public class StreamingDataUploadService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataUploadService.class);

    private final StreamingDataUploadRepository streamingDataUploadRepository;

    public StreamingDataUploadService(StreamingDataUploadRepository streamingDataUploadRepository) {
        this.streamingDataUploadRepository = streamingDataUploadRepository;
    }

    public Result<StreamingDataUpsertResult, Error> upsert(StreamingData streamingData, String username) {
        Optional<StreamingData> existingStreamingData = streamingDataUploadRepository.getStreamingDataByUsername(username);

        StreamingDataUpsertResult streamingDataUpsertResult;
        if (existingStreamingData.isPresent()) {
            Optional<StreamingDataUpsertResult> updateResult = streamingDataUploadRepository
                    .updateStreamingData(combineStreamingData(existingStreamingData.get(), streamingData), username);
            if (updateResult.isEmpty()) {
                return new Result.Failure<>(new Error("Failure updating streaming data - %s for username - %s ".formatted(streamingData, username)));
            }
            streamingDataUpsertResult = updateResult.get();
            LOG.info("Updated streaming data for user - {}", username);
        } else {
            Optional<StreamingDataUpsertResult> insertResult = streamingDataUploadRepository
                    .insertStreamingData(streamingData, username);
            if (insertResult.isEmpty()) {
                return new Result.Failure<>(new Error("Failure inserting streaming data - %s for username - %s ".formatted(streamingData, username)));
            }
            streamingDataUpsertResult = insertResult.get();
            LOG.info("Inserted streaming data for user - {}", username);
        }
        streamingData.streamData().forEach(streamData -> streamingDataUploadRepository.insertStreamData(streamData, username));
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
