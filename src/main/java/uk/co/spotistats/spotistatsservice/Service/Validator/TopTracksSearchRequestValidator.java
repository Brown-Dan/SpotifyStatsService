package uk.co.spotistats.spotistatsservice.Service.Validator;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.ErrorKey;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataUploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TopTracksSearchRequestValidator {

    private final StreamingDataUploadService streamingDataUploadService;

    public TopTracksSearchRequestValidator(StreamingDataUploadService streamingDataUploadService) {
        this.streamingDataUploadService = streamingDataUploadService;
    }

    public Errors validate(TopTracksSearchRequest topTracksSearchRequest) {
        List<Error> errors = new ArrayList<>();

        validateLimit(topTracksSearchRequest).ifPresent(errors::add);
        validateRanked(topTracksSearchRequest).ifPresent(errors::add);
        validatePage(topTracksSearchRequest).ifPresent(errors::add);

        return Errors.fromErrors(errors);
    }

    private Optional<Error> validateLimit(TopTracksSearchRequest topTracksSearchRequest) {
        if (topTracksSearchRequest.limit() < 1 || topTracksSearchRequest.limit() > 50) {
            return Optional.of(Error.requestParamContentViolation("limit", "'limit' must be between 1-50 - provided - %s".formatted(topTracksSearchRequest.limit())));
        } else {
            return Optional.empty();
        }
    }

    private Optional<Error> validatePage(TopTracksSearchRequest topTracksSearchRequest) {
        if (topTracksSearchRequest.limit() * topTracksSearchRequest.page() > 100){
            return Optional.of(Error.requestParamContentViolation("page", "maximum 100 results available - page multiplied by limit must be <= 100"));
        }
        if (topTracksSearchRequest.page() > 100 || topTracksSearchRequest.page() < 1){
            return Optional.of(Error.requestParamContentViolation("page", "'page' must be between 1-100 - provided - %s".formatted(topTracksSearchRequest.page())));
        }
        return Optional.empty();
    }

    private Optional<Error> validateRanked(TopTracksSearchRequest topTracksSearchRequest) {
        if (!streamingDataUploadService.hasStreamingData(topTracksSearchRequest.userId()) && topTracksSearchRequest.ranked()) {
            return Optional.of(new Error("ranked", "uploaded streaming data is required when 'ranked' is 'true'", ErrorKey.STREAMING_DATA_NOT_UPLOADED));
        }
        return Optional.empty();
    }
}
