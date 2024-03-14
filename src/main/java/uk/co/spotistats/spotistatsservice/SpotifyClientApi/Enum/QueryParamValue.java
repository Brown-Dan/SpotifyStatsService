package uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum;

import java.time.Instant;

public enum QueryParamValue {
    NOW(Instant.now().toString()),
    LONG_TERM("long_term");

    private final String value;

    QueryParamValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
