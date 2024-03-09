package uk.co.spotistats.spotistatsservice.Controller.Model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public sealed interface ApiResult<T, E> {

    static <T, E> ApiResult.Success<T, E> success(T value) {
        return new ApiResult.Success<>(value);
    }

    static <T, E> ApiResult.Failure<T, E> failure(E error) {
        return new ApiResult.Failure<>(error);
    }

    record Success<T, E>(@JsonUnwrapped T body) implements ApiResult<T, E> {
    }

    record Failure<T, E>(@JsonUnwrapped E error) implements ApiResult<T, E> {
    }
}
