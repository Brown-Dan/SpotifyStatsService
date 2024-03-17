package uk.co.spotistats.spotistatsservice.Service.Validator;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Service.SpotifyAuthService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class StreamDataSearchRequestValidator {

    private final SpotifyAuthService spotifyAuthService;

    public StreamDataSearchRequestValidator(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    public Errors validate(StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Error> errors = new ArrayList<>();
        validateQueryDatePeriod(streamingDataSearchRequest).ifPresent(errors::add);
        validateDayOfTheWeek(streamingDataSearchRequest).ifPresent(errors::add);
        validateMonth(streamingDataSearchRequest).ifPresent(errors::add);
        validateYear(streamingDataSearchRequest).ifPresent(errors::add);
        validateQueryTimePeriod(streamingDataSearchRequest).ifPresent(errors::add);
        validateOrderBy(streamingDataSearchRequest).ifPresent(errors::add);
        validateLimit(streamingDataSearchRequest).ifPresent(errors::add);
        validateCreatePlaylist(streamingDataSearchRequest).ifPresent(errors::add);
        return Errors.fromErrors(errors);
    }

    private Optional<Error> validateCreatePlaylist(StreamingDataSearchRequest streamingDataSearchRequest) {
        if (streamingDataSearchRequest.createPlaylist()) {
            if (!spotifyAuthService.isAuthorized(streamingDataSearchRequest.userId())) {
                return Optional.of(Error.searchRequestUnauthorized("createPlaylist"));
            }
        }
        return Optional.empty();
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
        if (streamingDataSearchRequest.endTime().isBefore(streamingDataSearchRequest.startTime())) {
            return Optional.of(Error.requestParamContentViolation("queryTimePeriod", "'startTime' must occur before 'endTime' - provided - %s-%s".formatted(streamingDataSearchRequest.startTime(), streamingDataSearchRequest.endTime())));
        }
        return Optional.empty();
    }

    private Optional<Error> validateDayOfTheWeek(StreamingDataSearchRequest searchRequest) {
        List<String> validValues = List.of("0", "1", "2", "3", "4", "5", "6");
        if (searchRequest.dayOfTheWeek() == null || validValues.contains(searchRequest.dayOfTheWeek())) {
            return Optional.empty();
        }
        return Optional.of(Error.requestParamContentViolation("dayOfTheWeek", "dayOfTheWeek must be a valid textual representation of a day. provided - %s".formatted(searchRequest.dayOfTheWeek())));
    }

    private Optional<Error> validateMonth(StreamingDataSearchRequest searchRequest) {
        List<String> validValues = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        if (searchRequest.month() == null || validValues.contains(searchRequest.month())) {
            return Optional.empty();
        }
        return Optional.of(Error.requestParamContentViolation("month", "month must be a valid textual representation of a month. provided - %s".formatted(searchRequest.month())));
    }

    private Optional<Error> validateYear(StreamingDataSearchRequest searchRequest) {
        try {
            if (searchRequest.year() == null){
                return Optional.empty();
            }
            int year = Integer.parseInt(searchRequest.year());
            if (year < 1970 || year > 3000) {
                return Optional.of(Error.requestParamContentViolation("year", "year must be between 1970-3000. provided - %s".formatted(searchRequest.year())));
            }
        } catch (NumberFormatException ignored) {
            return Optional.of(Error.requestParamContentViolation("year", "year must be between 1970-3000. provided - %s".formatted(searchRequest.year())));
        }
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
