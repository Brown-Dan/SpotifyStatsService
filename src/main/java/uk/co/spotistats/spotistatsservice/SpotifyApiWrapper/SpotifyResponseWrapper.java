package uk.co.spotistats.spotistatsservice.SpotifyApiWrapper;

import uk.co.autotrader.traverson.http.Response;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.SpotifyApiWrapper.Enum.SpotifyRequestError;

import java.util.Arrays;
import java.util.function.Function;

public class SpotifyResponseWrapper<T> {
    private final Response<T> response;

    SpotifyResponseWrapper(Response<T> response) {
        this.response = response;
    }

    public Response<T> execute() {
        return response;
    }

    boolean isFailure(){
        return !response.isSuccessful();
    }

    Response<T> getResponse(){
        return response;
    }

    public <U> Result<U, SpotifyRequestError> map(Function<T, U> successMappingFunction) {
        if (response.isSuccessful()) {
            return new Result.Success<>(successMappingFunction.apply(response.getResource()));
        }
        return new Result.Failure<>(mapError());
    }

    SpotifyRequestError mapError() {
        return Arrays.stream(SpotifyRequestError.values())
                .filter(error -> response.getStatusCode() == error.getStatus())
                .toList().getFirst();
    }
}
