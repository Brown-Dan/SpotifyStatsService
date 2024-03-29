package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.spotistats.spotistatsservice.Controller.Cleaner.StreamingDataRequestCleaner;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopArtistsSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.AdvancedTopArtist;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.TopArtists;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracks;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import java.util.function.Function;

@Controller
public class StreamingDataController {

    private final StreamingDataService streamingDataService;
    private final StreamingDataRequestCleaner streamingDataRequestCleaner;

    public StreamingDataController(StreamingDataService streamingDataService, StreamingDataRequestCleaner streamingDataRequestCleaner) {
        this.streamingDataService = streamingDataService;
        this.streamingDataRequestCleaner = streamingDataRequestCleaner;
    }

    @GetMapping(value = "tracks/top")
    public ResponseEntity<ApiResult<TopTracks, Errors>> getTopTracks(@RequestAttribute String userId, TopTracksSearchRequest searchRequest) {
        return get(streamingDataService::getTopTracks, streamingDataRequestCleaner.clean(searchRequest, userId));
    }

    @GetMapping(value = "tracks/get/{uri}")
    public ResponseEntity<ApiResult<AdvancedTrack, Errors>> getTrackByUri(@RequestAttribute String userId, @PathVariable String uri) {
        return get(streamingDataService::getByTrackUri, streamingDataRequestCleaner.clean(userId, uri));
    }

    @GetMapping(value = "tracks/recent")
    public ResponseEntity<ApiResult<RecentTracks, Errors>> getRecentTracks(@RequestAttribute String userId, RecentTracksSearchRequest searchRequest) {
        return get(streamingDataService::getRecentTracks, streamingDataRequestCleaner.clean(searchRequest, userId));
    }

    @GetMapping(value = "tracks/search")
    public ResponseEntity<ApiResult<SearchResponse, Errors>> search(@RequestAttribute String userId, StreamingDataSearchRequest searchRequest) {
        return get(streamingDataService::search, streamingDataRequestCleaner.clean(searchRequest, userId));
    }

    @GetMapping(value = "artists/top")
    public ResponseEntity<ApiResult<TopArtists, Errors>> getTopArtists(@RequestAttribute String userId, TopArtistsSearchRequest searchRequest){
        return get(streamingDataService::getTopArtists, streamingDataRequestCleaner.clean(searchRequest, userId));
    }

    @GetMapping(value = "artists/search")
    public ResponseEntity<ApiResult<AdvancedTopArtist, Errors>> getArtist(@RequestAttribute String userId, @RequestParam String artistName){
        return get(streamingDataService::getArtist, streamingDataRequestCleaner.cleanArtist(userId, artistName));
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
