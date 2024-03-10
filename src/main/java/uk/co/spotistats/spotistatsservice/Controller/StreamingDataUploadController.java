package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Controller.Mapper.MultipartFileToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataUploadService;

@RestController
@RequestMapping("{username}/data")
public class StreamingDataUploadController {

    private final StreamingDataUploadService streamingDataUploadService;
    private final MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    public StreamingDataUploadController(StreamingDataUploadService streamingDataUploadService, MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper) {
        this.streamingDataUploadService = streamingDataUploadService;
        this.multipartFileToStreamingDataMapper = multipartFileToStreamingDataMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<StreamingDataUpsertResult, Error>> upload(@RequestPart MultipartFile streamingDataFile,
                                                                              @PathVariable String username) {
        Result<StreamingData, Error> streamingDataMapResult = multipartFileToStreamingDataMapper.map(streamingDataFile);

        if (streamingDataMapResult.isFailure()) {
            return badRequest(streamingDataMapResult.getError());
        }
        Result<StreamingDataUpsertResult, Error> upsertResult =
                streamingDataUploadService.upsert(streamingDataMapResult.getValue(), username);

        return switch (upsertResult) {
            case Result.Success(StreamingDataUpsertResult streamingDataUpsertResult) -> ok(streamingDataUpsertResult);
            case Result.Failure(Error error) -> badRequest(error);
        };
    }

    @GetMapping(value = "/callback")
    public ResponseEntity<ApiResult<Object, Error>> temporaryCallback(@PathVariable String username, @RequestParam String code) {
        return ok(streamingDataUploadService.getTop(username, code));
    }

    private <T> ResponseEntity<ApiResult<T, Error>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Error>> badRequest(Error error) {
        return new ResponseEntity<>(ApiResult.failure(error), HttpStatus.BAD_REQUEST);
    }
}
