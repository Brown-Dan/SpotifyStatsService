package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Controller.Mapper.MultipartFileToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataUploadService;

@RestController
@RequestMapping("{username}")
public class StreamingDataUploadController {

    private final StreamingDataUploadService streamingDataUploadService;
    private final MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    public StreamingDataUploadController(StreamingDataUploadService streamingDataUploadService, MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper) {
        this.streamingDataUploadService = streamingDataUploadService;
        this.multipartFileToStreamingDataMapper = multipartFileToStreamingDataMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<StreamingDataUpsertResult, Errors>> upload(@RequestPart MultipartFile streamingDataFile,
                                                                               @PathVariable String username) {
        Result<StreamingData, Error> streamingDataMapResult = multipartFileToStreamingDataMapper.map(streamingDataFile);

        if (streamingDataMapResult.isFailure()) {
            return badRequest(Errors.fromError(streamingDataMapResult.getError()));
        }
        Result<StreamingDataUpsertResult, Error> upsertResult =
                streamingDataUploadService.upsert(streamingDataMapResult.getValue(), username);

        return switch (upsertResult) {
            case Result.Success(StreamingDataUpsertResult streamingDataUpsertResult) -> ok(streamingDataUpsertResult);
            case Result.Failure(Error error) -> badRequest(Errors.fromError(error));
        };
    }

    // TODO will remove when frontend implemented
    @GetMapping(value = "/callback")
    public ResponseEntity<ApiResult<String, Errors>> temporaryCallback(@PathVariable String username, @RequestParam String code) {
        return ok("TEST ENDPOINT");
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> badRequest(Errors errors) {
        return new ResponseEntity<>(ApiResult.failure(errors), errors.httpStatus());
    }
}
