package uk.co.spotistats.spotistatsservice.Controller.Validator;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StreamDataSearchRequestValidator {

    public List<Error> validate(StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Error> errors = new ArrayList<>();
        validateQueryPeriod(streamingDataSearchRequest).ifPresent(errors::add);
        return errors;
    }

    private Optional<Error> validateQueryPeriod(StreamingDataSearchRequest streamingDataSearchRequest) {
        if (streamingDataSearchRequest.on() != null) {
            if (streamingDataSearchRequest.start() != null || streamingDataSearchRequest.end() != null) {
                return Optional.of(new Error("Must provide either 'on' or 'start' and 'end' parameters"));
            }
            return Optional.empty();
        }
        if (streamingDataSearchRequest.start() != null && streamingDataSearchRequest.end() != null) {
            if (streamingDataSearchRequest.start().isAfter(streamingDataSearchRequest.end())) {
                return Optional.of(new Error("Parameter 'start' must occur before parameter 'end'"));
            }
            return Optional.empty();
        }
        return Optional.of(new Error("Must provide either 'on' or 'start' and 'end' parameters"));
    }

}
