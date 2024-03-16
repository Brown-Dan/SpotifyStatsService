package uk.co.spotistats.spotistatsservice.Controller.Cleaner;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;

import static uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest.Builder.aTopTracksSearchRequest;

@Component
public class StreamingDataRequestCleaner {

    public TopTracksSearchRequest clean(TopTracksSearchRequest topTracksSearchRequest, String userId){
        return aTopTracksSearchRequest()
                .withLimit(topTracksSearchRequest.limit() == null ? 10 : topTracksSearchRequest.limit())
                .withPage(topTracksSearchRequest.page() == null ? 1 : topTracksSearchRequest.page())
                .withCreatePlaylist(topTracksSearchRequest.createPlaylist() != null)
                .withRanked(topTracksSearchRequest.ranked() != null)
                .withUserId(userId)
                .build();
    }
}
