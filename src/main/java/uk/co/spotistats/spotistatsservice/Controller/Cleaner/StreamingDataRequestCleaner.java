package uk.co.spotistats.spotistatsservice.Controller.Cleaner;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;

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
}
