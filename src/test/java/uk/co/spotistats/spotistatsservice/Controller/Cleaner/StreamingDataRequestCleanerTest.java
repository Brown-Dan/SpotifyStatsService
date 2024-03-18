package uk.co.spotistats.spotistatsservice.Controller.Cleaner;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopArtistsSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TrackUriSearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest.Builder.aRecentTracksSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.TopArtistsSearchRequest.Builder.aTopArtistsSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest.Builder.aTopTracksSearchRequest;

@ExtendWith(MockitoExtension.class)
public class StreamingDataRequestCleanerTest {

    private StreamingDataRequestCleaner streamingDataRequestCleaner;

    private static final String USER_ID = "userId";

    @BeforeEach
    void setUp() {
        streamingDataRequestCleaner = new StreamingDataRequestCleaner();
    }

    @Test
    void clean_givenTopTracksSearchRequest_shouldSetNonSuppliedFieldsToDefaultValues() {
        TopTracksSearchRequest searchRequest = aTopTracksSearchRequest().build();
        TopTracksSearchRequest expectedSearchRequest = aTopTracksSearchRequest()
                .withLimit(10)
                .withPage(1)
                .withCreatePlaylist(false)
                .withAdvanced(false)
                .withAuthData(null)
                .withUserId(USER_ID).build();

        TopTracksSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenTopTracksSearchRequest_shouldNotModifySuppliedFields() {
        TopTracksSearchRequest searchRequest = aTopTracksSearchRequest()
                .withLimit(50)
                .withAdvanced(true)
                .build();

        TopTracksSearchRequest expectedSearchRequest = aTopTracksSearchRequest()
                .withLimit(50)
                .withPage(1)
                .withCreatePlaylist(false)
                .withAdvanced(true)
                .withAuthData(null)
                .withUserId(USER_ID).build();

        TopTracksSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenRecentTracksSearchRequest_shouldSetNonSuppliedFieldsToDefaultValues() {
        RecentTracksSearchRequest searchRequest = aRecentTracksSearchRequest().build();
        RecentTracksSearchRequest expectedSearchRequest = aRecentTracksSearchRequest()
                .withLimit(10)
                .withCreatePlaylist(false)
                .withAuthData(null)
                .withUserId(USER_ID).build();

        RecentTracksSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenRecentTracksSearchRequest_shouldNotModifySuppliedFields() {
        RecentTracksSearchRequest searchRequest = aRecentTracksSearchRequest()
                .withLimit(50)
                .withCreatePlaylist(true)
                .build();
        RecentTracksSearchRequest expectedSearchRequest = aRecentTracksSearchRequest()
                .withLimit(50)
                .withCreatePlaylist(true)
                .withAuthData(null)
                .withUserId(USER_ID).build();

        RecentTracksSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenTopArtistsSearchRequest_shouldSetNonSuppliedFieldsToDefaultValues() {
        TopArtistsSearchRequest searchRequest = aTopArtistsSearchRequest().build();
        TopArtistsSearchRequest expectedSearchRequest = aTopArtistsSearchRequest()
                .withLimit(10)
                .withPage(1)
                .withAdvanced(false)
                .withAuthData(null)
                .withUserId(USER_ID).build();

        TopArtistsSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenTopArtistsSearchRequest_shouldNotModifySuppliedFields() {
        TopArtistsSearchRequest searchRequest = aTopArtistsSearchRequest()
                .withLimit(40)
                .withAdvanced(true)
                .withPage(2)
                .build();
        TopArtistsSearchRequest expectedSearchRequest = aTopArtistsSearchRequest()
                .withLimit(40)
                .withPage(2)
                .withAdvanced(true)
                .withAuthData(null)
                .withUserId(USER_ID).build();

        TopArtistsSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenStreamingDataSearchRequest_shouldSetNonSuppliedFieldsToDefaultValuesAndShouldCleanQueryPeriods() {
        StreamingDataSearchRequest searchRequest = aStreamingDataSearchRequest()
                .withDayOfTheWeek("fridaY")
                .withMonth("march")
                .build();

        StreamingDataSearchRequest expectedSearchRequest = aStreamingDataSearchRequest()
                .withLimit(10)
                .withCreatePlaylist(false)
                .withDayOfTheWeek("5")
                .withMonth("3")
                .withUserId(USER_ID).build();

        StreamingDataSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenStreamingDataSearchRequest_shouldNotModifySuppliedFields() {
        StreamingDataSearchRequest searchRequest = aStreamingDataSearchRequest()
                .withLimit(50)
                .withArtist("Lana Del Rey")
                .withCountry("PL")
                .withCreatePlaylist(true)
                .build();

        StreamingDataSearchRequest expectedSearchRequest = aStreamingDataSearchRequest()
                .withLimit(50)
                .withArtist("Lana Del Rey")
                .withCreatePlaylist(true)
                .withCountry("PL")
                .withUserId(USER_ID).build();

        StreamingDataSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(searchRequest, USER_ID);

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }

    @Test
    void clean_givenUserIdAndTrackUri_shouldReturnTrackUriSearchRequest() {
        TrackUriSearchRequest expectedSearchRequest = new TrackUriSearchRequest(USER_ID, "trackUri");

        TrackUriSearchRequest cleanedSearchRequest = streamingDataRequestCleaner.clean(USER_ID, "trackUri");

        assertThat(expectedSearchRequest).isEqualTo(cleanedSearchRequest);
    }
}
