package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import uk.co.autotrader.traverson.http.TextBody;

public interface AbstractSpotifyPostRequest {

    TextBody getBody();

    String getUrl();
}
