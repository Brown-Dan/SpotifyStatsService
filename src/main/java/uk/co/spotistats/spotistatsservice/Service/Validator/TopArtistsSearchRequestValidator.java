package uk.co.spotistats.spotistatsservice.Service.Validator;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopArtistsSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.ErrorKey;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataUploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TopArtistsSearchRequestValidator {

    private final StreamingDataUploadService streamingDataUploadService;

    public TopArtistsSearchRequestValidator(StreamingDataUploadService streamingDataUploadService) {
        this.streamingDataUploadService = streamingDataUploadService;
    }

    public Errors validate(TopArtistsSearchRequest searchRequest) {
        List<Error> errors = new ArrayList<>();

        validateLimit(searchRequest).ifPresent(errors::add);
        validateAdvanced(searchRequest).ifPresent(errors::add);
        validatePage(searchRequest).ifPresent(errors::add);

        return Errors.fromErrors(errors);
    }

    private Optional<Error> validateLimit(TopArtistsSearchRequest searchRequest) {
        if (searchRequest.limit() < 1 || searchRequest.limit() > 50) {
            return Optional.of(Error.requestParamContentViolation("limit", "'limit' must be between 1-50 - provided - %s".formatted(searchRequest.limit())));
        } else {
            return Optional.empty();
        }
    }

    private Optional<Error> validatePage(TopArtistsSearchRequest searchRequest) {
        if (searchRequest.limit() * searchRequest.page() > 100) {
            return Optional.of(Error.requestParamContentViolation("page", "maximum 100 results available - page multiplied by limit must be <= 100"));
        }
        if (searchRequest.page() > 100 || searchRequest.page() < 1) {
            return Optional.of(Error.requestParamContentViolation("page", "'page' must be between 1-100 - provided - %s".formatted(searchRequest.page())));
        }
        return Optional.empty();
    }

    private Optional<Error> validateAdvanced(TopArtistsSearchRequest searchRequest) {
        if (!streamingDataUploadService.hasStreamingData(searchRequest.userId()) && searchRequest.advanced()) {
            return Optional.of(new Error("advanced", "uploaded streaming data is required when for 'advanced' insights", ErrorKey.STREAMING_DATA_NOT_UPLOADED));
        }
        return Optional.empty();
    }
}
