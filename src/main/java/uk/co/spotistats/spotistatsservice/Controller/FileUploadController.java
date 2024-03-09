package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Controller.Mapper.MultipartFileToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUploadResponse;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.FileUploadService;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    public FileUploadController(FileUploadService fileUploadService, MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper) {
        this.fileUploadService = fileUploadService;
        this.multipartFileToStreamingDataMapper = multipartFileToStreamingDataMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<StreamingDataUploadResponse, Error>> upload(@RequestPart MultipartFile file) {
        Result<StreamingData, Error> fileMapResult = multipartFileToStreamingDataMapper.map(file);

        if (fileMapResult.isFailure()) {
            return badRequest(fileMapResult.getError());
        }
        Result<StreamingDataUploadResponse, Error> streamingDataUploadResponseResult = fileUploadService.uploadFile(fileMapResult.getValue());

        return switch (streamingDataUploadResponseResult) {
            case Result.Success(StreamingDataUploadResponse streamingDataUploadResponse) ->
                    ok(streamingDataUploadResponse);
            case Result.Failure(Error error) -> badRequest(error);
        };
    }

    private <T> ResponseEntity<ApiResult<T, Error>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Error>> badRequest(Error error) {
        return new ResponseEntity<>(ApiResult.failure(error), HttpStatus.BAD_REQUEST);
    }
}
