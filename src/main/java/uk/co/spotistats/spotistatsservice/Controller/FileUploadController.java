package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Domain.Response.FileUploadResponse;
import uk.co.spotistats.spotistatsservice.Service.FileUploadService;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestPart(value = "file") MultipartFile file) {
        FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(file);
        return ok(fileUploadResponse);
    }

    private <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
}
