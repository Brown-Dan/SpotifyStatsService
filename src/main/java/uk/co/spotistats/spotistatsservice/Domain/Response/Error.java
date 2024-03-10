package uk.co.spotistats.spotistatsservice.Domain.Response;

public record Error(String message) {

    public static Error authorizationDetailsPresent(String username){
        return new Error("Authorization details already present for user - %s".formatted(username));
    }
}
