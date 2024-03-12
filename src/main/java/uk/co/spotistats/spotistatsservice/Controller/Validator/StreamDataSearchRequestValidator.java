package uk.co.spotistats.spotistatsservice.Controller.Validator;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class StreamDataSearchRequestValidator {

    public Errors validate(StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Error> errors = new ArrayList<>();
        validateQueryPeriod(streamingDataSearchRequest).ifPresent(errors::add);
        validateOrderBy(streamingDataSearchRequest).ifPresent(errors::add);
        validateLimit(streamingDataSearchRequest).ifPresent(errors::add);
        return Errors.fromErrors(errors);
    }

    private Optional<Error> validateLimit(StreamingDataSearchRequest streamingDataSearchRequest) {
        if (streamingDataSearchRequest.limit() == null) {
            return Optional.of(Error.requestParamNotSupplied("limit", "'limit' must be supplied within request parameters"));
        }
        if (streamingDataSearchRequest.limit() > 50 || streamingDataSearchRequest.limit() < 0) {
            return Optional.of(Error.requestParamContentViolation("limit", "'limit' must be between 0 and 50 - provided - %s".formatted(streamingDataSearchRequest.limit())));
        }
        return Optional.empty();
    }

    private Optional<Error> validateOrderBy(StreamingDataSearchRequest streamingDataSearchRequest) {
        if (Arrays.stream(StreamDataSearchRequestOrderBy.values()).map(StreamDataSearchRequestOrderBy::name).toList().contains(streamingDataSearchRequest.orderBy())) {
            return Optional.empty();
        } else {
            return Optional.of(Error.requestParamContentViolation("orderBy", "'order' value must be equal to one of the following - %s"
                    .formatted(Arrays.toString(StreamDataSearchRequestOrderBy.values()))));
        }
    }

    private Optional<Error> validateQueryPeriod(StreamingDataSearchRequest streamingDataSearchRequest) {
        if (streamingDataSearchRequest.on() != null) {
            if (streamingDataSearchRequest.start() != null || streamingDataSearchRequest.end() != null) {
                return Optional.of(Error.requestParamNotSupplied("queryPeriod", "must supply either 'on' or 'start' and 'end' parameters"));
            }
            return Optional.empty();
        }
        if (streamingDataSearchRequest.start() != null && streamingDataSearchRequest.end() != null) {
            if (streamingDataSearchRequest.start().isAfter(streamingDataSearchRequest.end())) {
                return Optional.of(Error.requestParamContentViolation("queryPeriod", "'start' must occur before 'end' - provided - %s-%s".formatted(streamingDataSearchRequest.start(), streamingDataSearchRequest.end())));
            }
            return Optional.empty();
        }
        return Optional.of(Error.requestParamNotSupplied("queryPeriod", "must supply either 'on' or 'start' and 'end' parameters"));
    }
}
