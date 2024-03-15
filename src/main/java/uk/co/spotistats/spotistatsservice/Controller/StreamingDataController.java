package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.RankedStreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.util.function.Function;

import static uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest.Builder.aSpotifySearchRequest;

@Controller
@RequestMapping("{username}")
public class StreamingDataController {

    private final StreamingDataService streamingDataService;

    public StreamingDataController(StreamingDataService streamingDataService) {
        this.streamingDataService = streamingDataService;
    }

    @GetMapping(value = "/recent")
    public ResponseEntity<ApiResult<StreamingData, Errors>> getRecentStreams(@PathVariable String username, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "false") boolean createPlaylist) {
        return get(streamingDataService::getRecentStreams, aSpotifySearchRequest().withUserId(username).withLimit(limit).withCreatePlaylist(createPlaylist).build());
    }

    @GetMapping(value = "/top")
    public ResponseEntity<ApiResult<RankedStreamingData, Errors>> getTopStreams(@PathVariable String username, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "false") boolean createPlaylist) {
        return get(streamingDataService::getTopStreams, aSpotifySearchRequest().withUserId(username).withLimit(limit).withCreatePlaylist(createPlaylist).build());
    }

    @GetMapping(value = "/search")
    public ResponseEntity<ApiResult<StreamingData, Errors>> search(@PathVariable String username, StreamingDataSearchRequest streamingDataSearchRequest) {
        return get(streamingDataService::search, streamingDataSearchRequest.cloneBuilder().withUsername(username).build());
    }

    private <T, U> ResponseEntity<ApiResult<T, Errors>> get(Function<U, Result<T, Errors>> function, U parameter) {
        Result<T, Errors> result = function.apply(parameter);
        return switch (result) {
            case Result.Success(T success) -> ok(success);
            case Result.Failure(Errors errors) -> buildErrorResponse(errors);
        };
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> buildErrorResponse(Errors errors) {
        return new ResponseEntity<>(ApiResult.failure(errors), errors.httpStatus());
    }
}
