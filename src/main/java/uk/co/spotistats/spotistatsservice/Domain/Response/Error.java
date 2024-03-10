package uk.co.spotistats.spotistatsservice.Domain.Response;

public record Error(String message) {

    public static Error authorizationDetailsPresent(String username) {
        return new Error("Authorization details already present for user - %s".formatted(username));
    }

    public static Error authorizationDetailsNotPresent(String username) {
        return new Error("No authentication data found for user - %s".formatted(username));
    }

    public static Error failedToRefreshAccessToken(String username, int responseCode) {
        return new Error("Failed to refresh access token for user - %s received %s response"
                .formatted(username, responseCode));
    }
}
