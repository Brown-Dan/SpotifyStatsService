package uk.co.spotistats.spotistatsservice.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Domain.Response.FileUploadResponse;

import static uk.co.spotistats.spotistatsservice.Domain.Response.FileUploadResponse.Builder.aFileUploadResponse;

@Service
public class FileUploadService {

    public FileUploadResponse uploadFile(MultipartFile file) {
        return aFileUploadResponse()
                .withFileName(file.getName())
                .withContentType(file.getContentType())
                .withFileSize(file.getSize()).build();
    }
}
