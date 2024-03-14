package uk.co.spotistats.spotistatsservice.Domain.Request;

import org.jooq.Condition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

import static uk.co.spotistats.generated.tables.StreamData.STREAM_DATA;

public enum ConditionBuilder {
    USERNAME(username -> STREAM_DATA.USERNAME.eq((String) username), StreamingDataSearchRequest::username),
    ON_DATE(date -> STREAM_DATA.DATE_TIME.eq((LocalDateTime) date), StreamingDataSearchRequest::onDate),
    URI(uri -> STREAM_DATA.TRACK_URI.eq((String) uri), StreamingDataSearchRequest::uri),
    ALBUM(album -> STREAM_DATA.ALBUM_NAME.eq((String) album), StreamingDataSearchRequest::album),
    ARIST(artist -> STREAM_DATA.ARTIST_NAME.eq((String) artist), StreamingDataSearchRequest::artist),
    COUNTRY(country -> STREAM_DATA.COUNTRY.eq((String) country), StreamingDataSearchRequest::country),
    TRACK_NAME(trackName -> STREAM_DATA.TRACK_NAME.eq((String) trackName), StreamingDataSearchRequest::trackName),
    PLATFORM(platform -> STREAM_DATA.PLATFORM.eq((String) platform), StreamingDataSearchRequest::platform),
    START_DATE(startDate -> STREAM_DATA.DATE.ge((LocalDate) startDate), StreamingDataSearchRequest::startDate),
    END_DATE(endDate -> STREAM_DATA.DATE.le((LocalDate) endDate), StreamingDataSearchRequest::endDate),
    START_TIME(startTime -> STREAM_DATA.TIME.ge((LocalTime) startTime), StreamingDataSearchRequest::startTime),
    END_TIME(endTime -> STREAM_DATA.TIME.le((LocalTime) endTime), StreamingDataSearchRequest::endTime);

    private final Function<Object, Condition> condition;
    private final Function<StreamingDataSearchRequest, Object> getter;

    ConditionBuilder(Function<Object, Condition> condition, Function<StreamingDataSearchRequest, Object> getter) {
        this.condition = condition;
        this.getter = getter;
    }

    public Function<Object, Condition> getCondition() {
        return condition;
    }

    public Function<StreamingDataSearchRequest, Object> getGetter() {
        return getter;
    }
}
