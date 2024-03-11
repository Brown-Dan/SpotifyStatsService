package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.util.List;

@Controller
@RequestMapping("{username}/data")
public class StreamingDataController {

    private final StreamingDataService streamingDataService;

    public StreamingDataController(StreamingDataService streamingDataService) {
        this.streamingDataService = streamingDataService;
    }

    @GetMapping(value = "/recent")
    public ResponseEntity<ApiResult<StreamingData, Error>> getRecentStreams(@PathVariable String username) {
        return ok(streamingDataService.getRecentStreams(username).getValue());
    }

    @GetMapping(value = "/top")
    public ResponseEntity<ApiResult<StreamingData, Error>> getTopStreams(@PathVariable String username) {
        return ok(streamingDataService.getTopStreams(username).getValue());
    }

    @GetMapping(value = "/query")
    public ResponseEntity<ApiResult<StreamingData, List<Error>>> getStreamingData(@PathVariable String username, StreamingDataSearchRequest streamingDataSearchRequest) {
        Result<StreamingData, List<Error>> getStreamingDataResult = streamingDataService.get(username, streamingDataSearchRequest);

        return switch (getStreamingDataResult){
            case Result.Failure (List<Error> errors) -> badRequest(errors);
            case Result.Success (StreamingData streamingData) -> ok(streamingData);
        };
    }

    private <T, U> ResponseEntity<ApiResult<T, U>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T, U> ResponseEntity<ApiResult<T, U>> badRequest(U error) {
        return new ResponseEntity<>(ApiResult.failure(error), HttpStatus.BAD_REQUEST);
    }
}
