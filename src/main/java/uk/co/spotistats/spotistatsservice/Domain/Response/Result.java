package uk.co.spotistats.spotistatsservice.Domain.Response;

import java.util.NoSuchElementException;
import java.util.function.Function;

public sealed interface Result<T, E> {

    boolean isFailure();

    T getValue();

    E getError();

    default <U> Result<U, E> map(Function<T, U> transformer) {
        return switch (this) {
            case Success(T value) -> new Success<>(transformer.apply(value));
            case Failure(E error) -> new Failure<>(error);
        };
    }

    record Success<T, E>(T value) implements Result<T, E> {
        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public E getError() {
            throw new NoSuchElementException("No error present");
        }
    }

    record Failure<T, E>(E error) implements Result<T, E> {
        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public T getValue() {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public E getError() {
            return error;
        }
    }
}
