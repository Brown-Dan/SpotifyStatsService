package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import java.util.Map;

public interface AbstractSpotifyGetRequest {

    Map<String, String> getQueryParams();

    String getUrl();
}
