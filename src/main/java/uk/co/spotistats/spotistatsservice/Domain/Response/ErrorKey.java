package uk.co.spotistats.spotistatsservice.Domain.Response;

import org.springframework.http.HttpStatus;

public enum ErrorKey {
    FORBIDDEN_TO_UPDATE(HttpStatus.FORBIDDEN),
    REQUEST_PARAM_CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST),
    REQUEST_PARAM_NOT_SUPPLIED(HttpStatus.BAD_REQUEST),
    AUTHORIZATION_FAILURE(HttpStatus.UNAUTHORIZED),
    FAILED_TO_PARSE_DATA(HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    SPOTIFY_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS),
    STREAMING_DATA_NOT_UPLOADED(HttpStatus.FORBIDDEN),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND);

    private final HttpStatus httpStatus;

    ErrorKey(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
