package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

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

    private <T> ResponseEntity<ApiResult<T, Error>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Error>> badRequest(Error error) {
        return new ResponseEntity<>(ApiResult.failure(error), HttpStatus.BAD_REQUEST);
    }
}
