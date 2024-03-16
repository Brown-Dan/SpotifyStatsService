package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.spotistats.spotistatsservice.Controller.Cleaner.StreamingDataRequestCleaner;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.TopTracksResource;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.SpotifySearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.util.function.Function;

@Controller
@RequestMapping("{username}")
public class StreamingDataController {

    private final StreamingDataService streamingDataService;
    private final StreamingDataRequestCleaner streamingDataRequestCleaner;

    public StreamingDataController(StreamingDataService streamingDataService, StreamingDataRequestCleaner streamingDataRequestCleaner) {
        this.streamingDataService = streamingDataService;
        this.streamingDataRequestCleaner = streamingDataRequestCleaner;
    }

    @GetMapping(value = "/top")
    public ResponseEntity<ApiResult<TopTracksResource, Errors>> top(@PathVariable String username, TopTracksSearchRequest searchRequest) {
        return get(streamingDataService::getTopTracks, streamingDataRequestCleaner.clean(searchRequest, username));
    }

    @GetMapping(value = "/recent")
    public ResponseEntity<ApiResult<StreamingData, Errors>> getRecentStreams(@PathVariable String username, SpotifySearchRequest spotifySearchRequest) {
        return get(streamingDataService::getRecentStreams, spotifySearchRequest.cloneBuilder().withUserId(username).build());
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
