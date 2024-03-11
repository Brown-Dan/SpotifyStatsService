package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.util.List;

@Controller
@RequestMapping("{username}")
public class StreamingDataController {

    private final StreamingDataService streamingDataService;

    public StreamingDataController(StreamingDataService streamingDataService) {
        this.streamingDataService = streamingDataService;
    }

    @GetMapping(value = "/recent")
    public ResponseEntity<ApiResult<StreamingData, Error>> getRecentStreams(@PathVariable String username) {
        Result<StreamingData, Error> result = streamingDataService.getRecentStreams(username);
        return switch (result) {
            case Result.Success(StreamingData streamingData) -> ok(streamingData);
            case Result.Failure(Error error) -> badRequest(error);
        };
    }

    @GetMapping(value = "/top")
    public ResponseEntity<ApiResult<List<RankedStreamData>, Error>> getTopStreams(@PathVariable String username) {
        Result<List<RankedStreamData>, Error> result = streamingDataService.getTopStreams(username);
        return switch (result) {
            case Result.Success(List<RankedStreamData> rankedStreamData) -> ok(rankedStreamData);
            case Result.Failure(Error error) -> badRequest(error);
        };
    }

    @GetMapping(value = "/search")
    public ResponseEntity<ApiResult<StreamingData, List<Error>>> searchStreamingData(@PathVariable String username, StreamingDataSearchRequest streamingDataSearchRequest) {
        Result<StreamingData, List<Error>> getStreamingDataResult = streamingDataService.search(username, streamingDataSearchRequest);

        return switch (getStreamingDataResult) {
            case Result.Failure(List<Error> errors) -> badRequest(errors);
            case Result.Success(StreamingData streamingData) -> ok(streamingData);
        };
    }

    private <T, U> ResponseEntity<ApiResult<T, U>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T, U> ResponseEntity<ApiResult<T, U>> badRequest(U error) {
        return new ResponseEntity<>(ApiResult.failure(error), HttpStatus.BAD_REQUEST);
    }
}
