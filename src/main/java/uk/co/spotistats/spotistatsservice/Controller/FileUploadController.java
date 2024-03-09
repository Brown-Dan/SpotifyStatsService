package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.UserDataUploadResponse;
import uk.co.spotistats.spotistatsservice.Service.FileUploadService;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<UserDataUploadResponse, Error>> upload(@RequestPart(value = "file") MultipartFile file) {
        Result<UserDataUploadResponse, Error> fileUploadResult = fileUploadService.uploadFile(file);
        return switch (fileUploadResult) {
            case Result.Success(UserDataUploadResponse userDataUploadResponse) ->
                    ok(ApiResult.success(userDataUploadResponse));
            case Result.Failure(Error error) -> badRequest(ApiResult.failure(error));
        };
    }

    private <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    private <T> ResponseEntity<T> badRequest(T body) {
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
