package uk.co.spotistats.spotistatsservice.SpotifyClientApi.Enum;

public enum Header {
    AUTHORIZATION("authorization");

    private final String value;

    Header(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
