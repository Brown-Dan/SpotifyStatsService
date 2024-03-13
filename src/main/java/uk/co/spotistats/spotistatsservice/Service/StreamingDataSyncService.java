package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;

@Service
@EnableAsync
public class StreamingDataSyncService {

    private final StreamingDataRepository streamingDataRepository;
    private final StreamingDataService streamingDataService;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataSyncService.class);

    public StreamingDataSyncService(StreamingDataRepository streamingDataRepository, StreamingDataService streamingDataService) {
        this.streamingDataRepository = streamingDataRepository;
        this.streamingDataService = streamingDataService;
    }

    @Async
    @Scheduled(fixedRate = 60000)
    public void sync() {
        List<StreamingData> streamingData = streamingDataRepository.getUnsyncedStreamingData();
        streamingData.forEach(streamingDataService::syncFromRecent);
        LOG.info("Syncing streaming data for users - {}", streamingData);
    }
}
