package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;

import java.util.UUID;

import static uk.co.spotistats.generated.Tables.STREAM_DATA;
import static uk.co.spotistats.generated.tables.StreamingData.STREAMING_DATA;

@Component
public class StreamingDataRepository {

    private final DSLContext db;

    public StreamingDataRepository(DSLContext db) {
        this.db = db;
    }

    public UUID insertStreamingData(StreamingData streamingData, String username) {
        UUID uuid = UUID.randomUUID();
        db.insertInto(STREAMING_DATA)
                .set(STREAMING_DATA.ID, uuid)
                .set(STREAMING_DATA.USERNAME, username)
                .set(STREAMING_DATA.STREAM_COUNT, streamingData.streamCount())
                .set(STREAMING_DATA.FIRST_STREAM_DATE, streamingData.firstStreamDateTime().toLocalDate())
                .set(STREAMING_DATA.LAST_STREAM_DATA, streamingData.lastStreamDateTime().toLocalDate())
                .execute();
        return uuid;
    }

    @Async
    public void insertStreamData(StreamData streamData, UUID streamingDataId) {
        db.insertInto(STREAM_DATA)
                .set(STREAM_DATA.STREAMING_DATA_ID, streamingDataId)
                .set(STREAM_DATA.TIME_STREAMED, (int) streamData.timeStreamed())
                .set(STREAM_DATA.ALBUM_NAME, streamData.album())
                .set(STREAM_DATA.ARTIST_NAME, streamData.artist())
                .set(STREAM_DATA.DATE, streamData.streamDateTime().toLocalDate())
                .set(STREAM_DATA.TRACK_URI, streamData.trackUri())
                .set(STREAM_DATA.TRACK_NAME, streamData.name())
                .set(STREAM_DATA.PLATFORM, streamData.platform())
                .set(STREAM_DATA.COUNTRY, streamData.country()).execute();
    }
}
