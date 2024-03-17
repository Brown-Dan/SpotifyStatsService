package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.spotistats.spotistatsservice.Controller.Cleaner.StreamingDataRequestCleaner;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracksResource;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.util.function.Function;

@Controller
@RequestMapping("{userId}")
public class StreamingDataController {

    private final StreamingDataService streamingDataService;
    private final StreamingDataRequestCleaner streamingDataRequestCleaner;

    public StreamingDataController(StreamingDataService streamingDataService, StreamingDataRequestCleaner streamingDataRequestCleaner) {
        this.streamingDataService = streamingDataService;
        this.streamingDataRequestCleaner = streamingDataRequestCleaner;
    }

    @GetMapping(value = "/top")
    public ResponseEntity<ApiResult<TopTracksResource, Errors>> getTopTracks(@PathVariable String userId, TopTracksSearchRequest searchRequest) {
        return get(streamingDataService::getTopTracks, streamingDataRequestCleaner.clean(searchRequest, userId));
    }

    @GetMapping(value = "/get/{uri}")
    public ResponseEntity<ApiResult<AdvancedTrack, Errors>> getTrackByUri(@PathVariable String userId, @PathVariable String uri) {
        return get(streamingDataService::getByTrackUri, streamingDataRequestCleaner.clean(userId, uri));
    }

    @GetMapping(value = "/recent")
    public ResponseEntity<ApiResult<RecentTracks, Errors>> getRecentStreams(@PathVariable String userId, RecentTracksSearchRequest searchRequest) {
        return get(streamingDataService::getRecentStreams, streamingDataRequestCleaner.clean(searchRequest, userId));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<ApiResult<SearchResponse, Errors>> search(@PathVariable String userId, StreamingDataSearchRequest searchRequest) {
        return get(streamingDataService::search, streamingDataRequestCleaner.clean(searchRequest, userId));
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
