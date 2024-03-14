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
        validateQueryDatePeriod(streamingDataSearchRequest).ifPresent(errors::add);
        validateQueryTimePeriod(streamingDataSearchRequest).ifPresent(errors::add);
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

    private Optional<Error> validateQueryTimePeriod(StreamingDataSearchRequest streamingDataSearchRequest) {
        if (streamingDataSearchRequest.startTime() == null && streamingDataSearchRequest.endTime() == null) {
            return Optional.empty();
        }
        if (streamingDataSearchRequest.startTime() != null ^ streamingDataSearchRequest.endTime() != null) {
            return Optional.of(Error.requestParamNotSupplied("queryTimePeriod", "must supply none or both of 'startTime' and 'endTime' parameters"));
        }
        // TODO remove once conditions changed
//        if (streamingDataSearchRequest.endTime().isBefore(streamingDataSearchRequest.startTime())) {
//            return Optional.of(Error.requestParamContentViolation("queryTimePeriod", "'startTime' must occur before 'endTime' - provided - %s-%s".formatted(streamingDataSearchRequest.startTime(), streamingDataSearchRequest.endTime())));
//        }
        return Optional.empty();
    }

    private Optional<Error> validateQueryDatePeriod(StreamingDataSearchRequest streamingDataSearchRequest) {
        if (streamingDataSearchRequest.onDate() != null) {
            if (streamingDataSearchRequest.startDate() != null || streamingDataSearchRequest.endDate() != null) {
                return Optional.of(Error.requestParamNotSupplied("queryDatePeriod", "must supply either 'onDate' or 'startDate' and 'endDate' parameters"));
            }
            return Optional.empty();
        }
        if (streamingDataSearchRequest.startDate() != null && streamingDataSearchRequest.endDate() != null) {
            if (streamingDataSearchRequest.startDate().isAfter(streamingDataSearchRequest.endDate())) {
                return Optional.of(Error.requestParamContentViolation("queryDatePeriod", "'startDate' must occur before 'endDate' - provided - %s-%s".formatted(streamingDataSearchRequest.startDate(), streamingDataSearchRequest.endDate())));
            }
            return Optional.empty();
        }
        return Optional.of(Error.requestParamNotSupplied("queryDatePeriod", "must supply either 'onDate' or 'startDate' and 'endDate' parameters"));
    }
}
