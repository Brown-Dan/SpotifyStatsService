package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUploadResponse;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;

@Service
public class FileUploadService {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadService.class);

    public Result<StreamingDataUploadResponse, Error> uploadFile(StreamingData streamingData) {
        return new Result.Success<>(new StreamingDataUploadResponse(streamingData.streamCount(), streamingData.firstStreamDateTime(), streamingData.lastStreamDateTime()));
    }
}
