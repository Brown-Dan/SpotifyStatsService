package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataInsertResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;

@Service
public class StreamingDataService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataService.class);

    public Result<StreamingDataInsertResult, Error> uploadFile(StreamingData streamingData) {
        return new Result.Success<>(new StreamingDataInsertResult(streamingData.streamCount(), streamingData.firstStreamDateTime(), streamingData.lastStreamDateTime()));
    }
}
