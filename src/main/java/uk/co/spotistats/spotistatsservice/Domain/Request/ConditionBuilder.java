package uk.co.spotistats.spotistatsservice.Domain.Request;

import org.jooq.Condition;

import java.time.LocalDateTime;
import java.util.function.Function;

import static uk.co.spotistats.generated.tables.StreamData.STREAM_DATA;

public enum ConditionBuilder {
    USERNAME(username -> STREAM_DATA.USERNAME.eq((String) username)),
    ON_DATE(date -> STREAM_DATA.DATE_TIME.eq((LocalDateTime) date)),
    URI(uri -> STREAM_DATA.TRACK_URI.eq((String) uri)),
    ALBUM(album -> STREAM_DATA.ALBUM_NAME.eq((String) album)),
    ARIST(artist -> STREAM_DATA.ARTIST_NAME.eq((String) artist)),
    TRACK_NAME(trackName -> STREAM_DATA.TRACK_NAME.eq((String) trackName)),
    PLATFORM(platform -> STREAM_DATA.PLATFORM.eq((String) platform)),

    ;

    private final Function<Object, Condition> condition;

    ConditionBuilder(Function<Object, Condition> condition) {
        this.condition = condition;
    }

    public Function<Object, Condition> getCondition() {
        return condition;
    }
}
