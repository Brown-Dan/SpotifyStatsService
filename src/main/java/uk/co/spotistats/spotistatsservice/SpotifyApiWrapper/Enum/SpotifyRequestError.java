package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum;

public enum SpotifyRequestError {
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    TOO_MANY_REQUESTS(429),
    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503);

    private final Integer status;

    SpotifyRequestError(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}