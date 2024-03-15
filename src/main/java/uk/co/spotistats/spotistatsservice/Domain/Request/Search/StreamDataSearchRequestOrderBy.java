package uk.co.spotistats.spotistatsservice.Domain.Request.Search;

import org.jooq.TableField;
import uk.co.spotistats.generated.tables.records.StreamDataRecord;

import static uk.co.spotistats.generated.Tables.STREAM_DATA;

public enum StreamDataSearchRequestOrderBy {
    DATE(STREAM_DATA.DATE_TIME);

    private final TableField<StreamDataRecord, ?> field;

    StreamDataSearchRequestOrderBy(TableField<StreamDataRecord, ?> field) {
        this.field = field;
    }

    public TableField<StreamDataRecord, ?> getField() {
        return field;
    }
}
