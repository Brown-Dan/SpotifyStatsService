package uk.co.spotistats.spotistatsservice.Domain.Request.Search;

import org.jooq.SortField;

import static uk.co.spotistats.generated.Tables.STREAM_DATA;

public enum StreamDataSearchRequestOrderBy {
    DATE_ASC(STREAM_DATA.DATE_TIME.asc()),
    DATE_DESC(STREAM_DATA.DATE_TIME.desc()),
    MS_STREAMED_DESC(STREAM_DATA.TIME_STREAMED.desc()),
    MS_STREAMED_ASC(STREAM_DATA.TIME_STREAMED.asc()),
    ARTIST_NAME_ASC(STREAM_DATA.ARTIST_NAME.asc()),
    ARTIST_NAME_DESC(STREAM_DATA.ARTIST_NAME.desc()),
    TRACK_NAME_ASC(STREAM_DATA.TRACK_NAME.asc()),
    TRACK_NAME_DESC(STREAM_DATA.TRACK_NAME.desc());

    private final SortField<?> field;

    StreamDataSearchRequestOrderBy(SortField<?> field) {
        this.field = field;
    }

    public SortField<?> getField() {
        return field;
    }
}
