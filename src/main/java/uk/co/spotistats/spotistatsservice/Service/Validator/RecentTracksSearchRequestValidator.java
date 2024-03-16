package uk.co.spotistats.spotistatsservice.Service.Validator;


import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Request.RecentTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RecentTracksSearchRequestValidator {

    public Errors validate(RecentTracksSearchRequest searchRequest) {
        List<Error> errors = new ArrayList<>();

        validateLimit(searchRequest).ifPresent(errors::add);

        return Errors.fromErrors(errors);
    }

    private Optional<Error> validateLimit(RecentTracksSearchRequest searchRequest) {
        if (searchRequest.limit() < 1 || searchRequest.limit() > 50) {
            return Optional.of(Error.requestParamContentViolation("limit", "'limit' must be between 1-50 - provided - %s".formatted(searchRequest.limit())));
        }
        return Optional.empty();
    }
}
