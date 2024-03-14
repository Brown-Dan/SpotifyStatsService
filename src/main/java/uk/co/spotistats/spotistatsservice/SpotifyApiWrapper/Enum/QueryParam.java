package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum;

public enum QueryParam {
    LIMIT("limit"),
    BEFORE("before"),
    TIME_RANGE("time_range");

    private final String value;

    QueryParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
