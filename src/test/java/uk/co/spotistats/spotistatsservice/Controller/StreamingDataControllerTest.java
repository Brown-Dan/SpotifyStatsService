package uk.co.spotistats.spotistatsservice.Controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.spotistats.spotistatsservice.Controller.Cleaner.StreamingDataRequestCleaner;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopArtistsSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TrackUriSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.TopArtists;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracks;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest.Builder.aRecentTracksSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.TopArtistsSearchRequest.Builder.aTopArtistsSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest.Builder.aTopTracksSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Response.AdvancedTrack.Builder.anAdvancedTrack;
import static uk.co.spotistats.spotistatsservice.Domain.Response.RecentTracks.RecentTracks.Builder.someRecentTracks;
import static uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse.Builder.aSearchResponse;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.AdvancedTopArtists.Builder.someAdvancedTopArtists;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Advanced.AdvancedRankedTracks.Builder.someAdvancedRankedTracks;

@ExtendWith(MockitoExtension.class)
public class StreamingDataControllerTest {

    @Mock
    private StreamingDataService streamingDataService;

    @Mock
    private StreamingDataRequestCleaner streamingDataRequestCleaner;

    private StreamingDataController streamingDataController;

    private static final String USER_ID = "userId";

    @BeforeEach
    void setUp() {
        streamingDataController = new StreamingDataController(streamingDataService, streamingDataRequestCleaner);
    }

    @Test
    void getTopTracks_givenUserIdAndTopTracksSearchRequest_shouldReturnTopTracks() {
        TopTracksSearchRequest searchRequest = aTopTracksSearchRequest().build();

        when(streamingDataRequestCleaner.clean((TopTracksSearchRequest) any(), any())).thenReturn(aTopTracksSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.getTopTracks(any())).thenReturn(new Result.Success<>(someAdvancedRankedTracks().build()));

        ResponseEntity<ApiResult<TopTracks, Errors>> result = streamingDataController.getTopTracks(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>(someAdvancedRankedTracks().build()));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).getTopTracks(aTopTracksSearchRequest().withUserId(USER_ID).build());
    }

    @Test
    void getTopTracks_givenUserIdAndTopTracksSearchRequest_ifErrorReturnedShouldHandleGracefully() {
        TopTracksSearchRequest searchRequest = aTopTracksSearchRequest().build();

        when(streamingDataRequestCleaner.clean((TopTracksSearchRequest) any(), any())).thenReturn(aTopTracksSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.getTopTracks(any())).thenReturn(new Result.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        ResponseEntity<ApiResult<TopTracks, Errors>> result = streamingDataController.getTopTracks(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).getTopTracks(aTopTracksSearchRequest().withUserId(USER_ID).build());
    }

    @Test
    void getTrackByUri_givenUserIdAndTrackUri_shouldReturnAdvancedTrackData() {
        TrackUriSearchRequest searchRequest = new TrackUriSearchRequest(USER_ID, "trackUri");

        when(streamingDataRequestCleaner.clean((String) any(), any())).thenReturn(searchRequest);
        when(streamingDataService.getByTrackUri(any())).thenReturn(new Result.Success<>(anAdvancedTrack().build()));

        ResponseEntity<ApiResult<AdvancedTrack, Errors>> result = streamingDataController.getTrackByUri(USER_ID, "trackUri");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>(anAdvancedTrack().build()));

        verify(streamingDataRequestCleaner).clean(USER_ID, "trackUri");
        verify(streamingDataService).getByTrackUri(searchRequest);
    }

    @Test
    void getByTrackUri_givenUserIdAndTrackUri_ifErrorReturnedShouldHandleGracefully() {
        TrackUriSearchRequest searchRequest = new TrackUriSearchRequest(USER_ID, "trackUri");

        when(streamingDataRequestCleaner.clean((String) any(), any())).thenReturn(searchRequest);
        when(streamingDataService.getByTrackUri(any())).thenReturn(new Result.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        ResponseEntity<ApiResult<AdvancedTrack, Errors>> result = streamingDataController.getTrackByUri(USER_ID, "trackUri");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        verify(streamingDataRequestCleaner).clean(USER_ID, "trackUri");
        verify(streamingDataService).getByTrackUri(searchRequest);
    }

    @Test
    void getRecentTracks_givenUserIdAndRecentTracksSearchRequest_shouldReturnRecentTracks() {
        RecentTracksSearchRequest searchRequest = aRecentTracksSearchRequest().build();

        when(streamingDataRequestCleaner.clean((RecentTracksSearchRequest) any(), any())).thenReturn(aRecentTracksSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.getRecentTracks(any())).thenReturn(new Result.Success<>(someRecentTracks().build()));

        ResponseEntity<ApiResult<RecentTracks, Errors>> result = streamingDataController.getRecentTracks(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>(someRecentTracks().build()));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).getRecentTracks(aRecentTracksSearchRequest().withUserId(USER_ID).build());
    }

    @Test
    void getRecentTracks_givenUserIdAndRecentTracksSearchRequest_ifErrorReturnedShouldHandleGracefully() {
        RecentTracksSearchRequest searchRequest = aRecentTracksSearchRequest().build();

        when(streamingDataRequestCleaner.clean((RecentTracksSearchRequest) any(), any())).thenReturn(aRecentTracksSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.getRecentTracks(any())).thenReturn(new Result.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        ResponseEntity<ApiResult<RecentTracks, Errors>> result = streamingDataController.getRecentTracks(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).getRecentTracks(aRecentTracksSearchRequest().withUserId(USER_ID).build());
    }

    @Test
    void search_givenUserIdAndStreamingDataSearchRequest_shouldReturnSearchResponse() {
        StreamingDataSearchRequest searchRequest = aStreamingDataSearchRequest().build();

        when(streamingDataRequestCleaner.clean((StreamingDataSearchRequest) any(), any())).thenReturn(aStreamingDataSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.search(any())).thenReturn(new Result.Success<>(aSearchResponse().build()));

        ResponseEntity<ApiResult<SearchResponse, Errors>> result = streamingDataController.search(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>(aSearchResponse().build()));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).search(aStreamingDataSearchRequest().withUserId(USER_ID).build());
    }

    @Test
    void search_givenUserIdAndStreamingDataSearchRequest_ifErrorReturnedShouldHandleGracefully() {
        StreamingDataSearchRequest searchRequest = aStreamingDataSearchRequest().build();

        when(streamingDataRequestCleaner.clean((StreamingDataSearchRequest) any(), any())).thenReturn(aStreamingDataSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.search(any())).thenReturn(new Result.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        ResponseEntity<ApiResult<SearchResponse, Errors>> result = streamingDataController.search(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).search(aStreamingDataSearchRequest().withUserId(USER_ID).build());
    }

    @Test
    void getTopArtists_givenUserIdAndTopArtistsSearchRequest_shouldReturnTopTracks() {
        TopArtistsSearchRequest searchRequest = aTopArtistsSearchRequest().build();

        when(streamingDataRequestCleaner.clean((TopArtistsSearchRequest) any(), any())).thenReturn(aTopArtistsSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.getTopArtists(any())).thenReturn(new Result.Success<>(someAdvancedTopArtists().build()));

        ResponseEntity<ApiResult<TopArtists, Errors>> result = streamingDataController.getTopArtists(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>(someAdvancedTopArtists().build()));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).getTopArtists(aTopArtistsSearchRequest().withUserId(USER_ID).build());
    }

    @Test
    void getTopArtists_givenUserIdAndTopArtistsSearchRequest_ifErrorReturnedShouldHandleGracefully() {
        TopArtistsSearchRequest searchRequest = aTopArtistsSearchRequest().build();

        when(streamingDataRequestCleaner.clean((TopArtistsSearchRequest) any(), any())).thenReturn(aTopArtistsSearchRequest().withUserId(USER_ID).build());
        when(streamingDataService.getTopArtists(any())).thenReturn(new Result.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        ResponseEntity<ApiResult<TopArtists, Errors>> result = streamingDataController.getTopArtists(USER_ID, searchRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(Errors.fromError(Error.spotifyRateLimitExceeded())));

        verify(streamingDataRequestCleaner).clean(searchRequest, USER_ID);
        verify(streamingDataService).getTopArtists(aTopArtistsSearchRequest().withUserId(USER_ID).build());
    }
}
