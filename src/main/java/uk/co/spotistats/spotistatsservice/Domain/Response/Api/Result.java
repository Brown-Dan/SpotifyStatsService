package uk.co.spotistats.spotistatsservice.Domain.Response.Api;

import java.util.NoSuchElementException;

public sealed interface Result<T, E> {

    boolean isFailure();

    T getValue();

    E getError();

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
