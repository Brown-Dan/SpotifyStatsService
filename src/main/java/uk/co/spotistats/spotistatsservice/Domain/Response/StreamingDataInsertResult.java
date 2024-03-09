package uk.co.spotistats.spotistatsservice.Domain.Response;

import java.time.LocalDateTime;

public record StreamingDataInsertResult(Integer streamCount,
                                        LocalDateTime firstStreamDateTime,
                                        LocalDateTime lastStreamDateTime) {

}
