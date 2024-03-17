package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Controller.Mapper.MultipartFileToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.Upload.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataUploadService;

@RestController
@RequestMapping("data")
public class StreamingDataUploadController {

    private final StreamingDataUploadService streamingDataUploadService;
    private final MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    public StreamingDataUploadController(StreamingDataUploadService streamingDataUploadService, MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper) {
        this.streamingDataUploadService = streamingDataUploadService;
        this.multipartFileToStreamingDataMapper = multipartFileToStreamingDataMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<StreamingDataUpsertResult, Errors>> upload(@RequestPart MultipartFile streamingDataFile,
                                                                               @RequestAttribute String username) {
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

    private <T> ResponseEntity<ApiResult<T, Errors>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> badRequest(Errors errors) {
        return new ResponseEntity<>(ApiResult.failure(errors), errors.httpStatus());
    }
}
