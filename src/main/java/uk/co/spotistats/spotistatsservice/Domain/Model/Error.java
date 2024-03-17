package uk.co.spotistats.spotistatsservice.Domain.Model;

import uk.co.spotistats.spotistatsservice.Domain.Response.Api.ErrorKey;

public record Error(String field, String message, ErrorKey key) {

    public static Error forbiddenToUpdate(String field, String username) {
        return new Error(field, "Forbidden to update '%s' for user - %s"
                .formatted(field, username), ErrorKey.FORBIDDEN_TO_UPDATE);
    }

    public static Error notFound(String field, String username) {
        return new Error(field, "%s not found for user - %s".formatted(field, username), ErrorKey.ENTITY_NOT_FOUND);
    }

    public static Error userNotWhitelisted() {
        return new Error("user", "user is not registered in the developer dashboard", ErrorKey.AUTHORIZATION_FAILURE);
    }

    public static Error requestParamContentViolation(String field, String message) {
        return new Error(field, message, ErrorKey.REQUEST_PARAM_CONSTRAINT_VIOLATION);
    }

    public static Error requestParamNotSupplied(String field, String message) {
        return new Error(field, message, ErrorKey.REQUEST_PARAM_NOT_SUPPLIED);
    }

    public static Error failedToParseData(String field, String message) {
        return new Error(field, message, ErrorKey.FAILED_TO_PARSE_DATA);
    }

    public static Error failedToRefreshAccessToken(String username, int responseCode) {
        return new Error("%s.spotifyAuthorizationData".formatted(username), "Failed to refresh access token for user - %s received %s response"
                .formatted(username, responseCode), ErrorKey.AUTHORIZATION_FAILURE);
    }

    public static Error unknownError(String field, String message) {
        return new Error(field, message, ErrorKey.UNKNOWN_ERROR);
    }

    public static Error searchRequestUnauthorized(String field) {
        return new Error(field, "Spotify authorization is required for parameter '%s'".formatted(field), ErrorKey.AUTHORIZATION_FAILURE);
    }

    public static Error spotifyRateLimitExceeded() {
        return new Error("spotify.API", "Exceeded spotify rate limit - try again in 60s", ErrorKey.SPOTIFY_RATE_LIMIT_EXCEEDED);
    }
}
