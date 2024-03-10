package uk.co.spotistats.spotistatsservice.Domain;

import java.time.LocalDate;

public record StreamingDataSearchRequest(
        LocalDate start,
        LocalDate end,
        LocalDate on,
        String country,
        String uri,
        String trackName,
        String artist,
        String album,
        String platform
) {
}
