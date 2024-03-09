package uk.co.spotistats.spotistatsservice.Domain.Response;

import java.time.LocalDateTime;

public record StreamingDataUploadResponse(Integer streamCount, LocalDateTime firstStreamDateTime, LocalDateTime lastStreamDateTime) {

}
