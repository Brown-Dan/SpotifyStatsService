package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataUploadRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.aStreamingData;

@Service
public class StreamingDataUploadService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataUploadService.class);

    private final StreamingDataUploadRepository streamingDataUploadRepository;

    public StreamingDataUploadService(StreamingDataUploadRepository streamingDataUploadRepository) {
        this.streamingDataUploadRepository = streamingDataUploadRepository;
    }

    public boolean hasStreamingData(String userId){
        Optional<StreamingData> existingStreamingData = streamingDataUploadRepository.getStreamingDataByUsername(userId);
        return existingStreamingData.isPresent();
    }

    public Result<StreamingDataUpsertResult, Error> upsert(StreamingData streamingData, String userId) {
        Optional<StreamingData> existingStreamingData = streamingDataUploadRepository.getStreamingDataByUsername(userId);

        StreamingDataUpsertResult streamingDataUpsertResult;
        if (existingStreamingData.isPresent()) {
            Optional<StreamingDataUpsertResult> updateResult = streamingDataUploadRepository
                    .updateStreamingData(combineStreamingData(existingStreamingData.get(), streamingData), userId);
            if (updateResult.isEmpty()) {
                return new Result.Failure<>(Error.unknownError(null, "Failure updating streaming data - %s for userId - %s ".formatted(streamingData, userId)));
            }
            streamingDataUpsertResult = updateResult.get();
            LOG.info("Updated streaming data for user - {}", userId);
        } else {
            Optional<StreamingDataUpsertResult> insertResult = streamingDataUploadRepository
                    .insertStreamingData(streamingData, userId);
            if (insertResult.isEmpty()) {
                return new Result.Failure<>(Error.unknownError(null, "Failure inserting streaming data - %s for userId - %s ".formatted(streamingData, userId)));
            }
            streamingDataUpsertResult = insertResult.get();
            LOG.info("Inserted streaming data for user - {}", userId);
        }
        streamingData.streamData().forEach(streamData -> streamingDataUploadRepository.insertStreamData(streamData, userId));
        return new Result.Success<>(streamingDataUpsertResult);
    }

    private StreamingData combineStreamingData(StreamingData originalData, StreamingData newData) {
        LocalDateTime firstStreamDateTime = originalData.firstStreamDateTime().isBefore(newData.firstStreamDateTime())
                ? originalData.firstStreamDateTime() : newData.firstStreamDateTime();
        LocalDateTime lastStreamDateTime = originalData.lastStreamDateTime().isAfter(newData.lastStreamDateTime())
                ? originalData.lastStreamDateTime() : newData.lastStreamDateTime();

        return aStreamingData()
                .withSize(originalData.size() + newData.size())
                .withFirstStreamDateTime(firstStreamDateTime)
                .withLastStreamDateTime(lastStreamDateTime)
                .withStreamData(newData.streamData())
                .build();
    }
}
