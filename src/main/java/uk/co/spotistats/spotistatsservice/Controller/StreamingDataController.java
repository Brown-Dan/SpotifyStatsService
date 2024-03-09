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
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataInsertResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

@RestController
@RequestMapping("/data")
public class StreamingDataController {

    private final StreamingDataService streamingDataService;
    private final MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    public StreamingDataController(StreamingDataService streamingDataService, MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper) {
        this.streamingDataService = streamingDataService;
        this.multipartFileToStreamingDataMapper = multipartFileToStreamingDataMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<StreamingDataInsertResult, Error>> upload(@RequestPart MultipartFile streamingDataFile) {
        Result<StreamingData, Error> streamingDataMapResult = multipartFileToStreamingDataMapper.map(streamingDataFile);

        if (streamingDataMapResult.isFailure()) {
            return badRequest(streamingDataMapResult.getError());
        }
        Result<StreamingDataInsertResult, Error> insertResult =
                streamingDataService.insert(streamingDataMapResult.getValue());

        return switch (insertResult) {
            case Result.Success(StreamingDataInsertResult streamingDataInsertResult) -> ok(streamingDataInsertResult);
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