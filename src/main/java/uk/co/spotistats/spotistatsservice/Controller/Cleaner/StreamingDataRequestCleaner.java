package uk.co.spotistats.spotistatsservice.Controller.Cleaner;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.DayOfTheWeek;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.Month;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TrackUriSearchRequest;

import java.util.Arrays;

import static uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest.Builder.aRecentTracksSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest.Builder.aTopTracksSearchRequest;

@Component
public class StreamingDataRequestCleaner {

    public TopTracksSearchRequest clean(TopTracksSearchRequest searchRequest, String userId){
        return aTopTracksSearchRequest()
                .withLimit(searchRequest.limit() == null ? 10 : searchRequest.limit())
                .withPage(searchRequest.page() == null ? 1 : searchRequest.page())
                .withCreatePlaylist(searchRequest.createPlaylist() != null && searchRequest.createPlaylist())
                .withAdvanced(searchRequest.advanced() != null && searchRequest.advanced())
                .withUserId(userId)
                .build();
    }

    public RecentTracksSearchRequest clean(RecentTracksSearchRequest searchRequest, String userId){
        return aRecentTracksSearchRequest()
                .withLimit(searchRequest.limit() == null ? 10 : searchRequest.limit())
                .withCreatePlaylist(searchRequest.createPlaylist() != null && searchRequest.createPlaylist())
                .withUserId(userId)
                .build();
    }

    public StreamingDataSearchRequest clean(StreamingDataSearchRequest searchRequest, String userId){
        StreamingDataSearchRequest.Builder cleanedSearchRequest = searchRequest.cloneBuilder();
        if (searchRequest.dayOfTheWeek() != null && Arrays.stream(DayOfTheWeek.values()).map(DayOfTheWeek::name).toList().contains(searchRequest.dayOfTheWeek().toUpperCase())) {
            cleanedSearchRequest.withDayOfTheWeek(DayOfTheWeek.valueOf(searchRequest.dayOfTheWeek().toUpperCase()).getDbRepresentation().toString());
        }
        if (searchRequest.month() != null && Arrays.stream(Month.values()).map(Month::name).toList().contains(searchRequest.month().toUpperCase())) {
            cleanedSearchRequest.withMonth(Month.valueOf(searchRequest.month().toUpperCase()).getDbRepresentation().toString());
        }
        return cleanedSearchRequest
                .withCreatePlaylist(searchRequest.createPlaylist() != null && searchRequest.createPlaylist())
                .withUserId(userId)
                .build();
    }

    public TrackUriSearchRequest clean(String userId, String trackUri){
        return new TrackUriSearchRequest(userId, trackUri);
    }
}
