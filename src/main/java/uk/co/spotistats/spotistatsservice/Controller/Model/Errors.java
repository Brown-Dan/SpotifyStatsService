package uk.co.spotistats.spotistatsservice.Controller.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.ErrorKey;

import java.util.List;
import java.util.OptionalInt;

public record Errors(List<Error> errors, @JsonIgnore HttpStatus httpStatus) {

    public static Errors fromError(Error error) {
        return new Errors(List.of(error), error.key().getHttpStatus());
    }

    public static Errors fromErrors(List<Error> errors) {
        OptionalInt status = errors.stream().map(Error::key).map(ErrorKey::getHttpStatus).mapToInt(HttpStatus::value).max();
        if (status.isPresent()) {
            return new Errors(errors, HttpStatus.valueOf(status.getAsInt()));
        }
        return new Errors(errors, HttpStatus.BAD_REQUEST);
    }
}
