package uk.co.spotistats.spotistatsservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.UserDataUploadResponse;

import java.io.IOException;

@Service
public class FileUploadService {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadService.class);

    public Result<UserDataUploadResponse, Error> uploadFile(MultipartFile file) {
        try {
            String asJson = new String(file.getBytes());
        } catch (IOException ioException){
            LOG.error("Failed to parse file", ioException);
            return new Result.Failure<>(new Error("Failed to parse file"));
        }
        return new Result.Success<>(new UserDataUploadResponse(null, null, 3));
    }
}
