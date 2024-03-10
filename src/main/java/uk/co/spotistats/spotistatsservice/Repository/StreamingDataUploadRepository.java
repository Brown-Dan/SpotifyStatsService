package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Domain.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;

import java.util.Optional;

import static uk.co.spotistats.generated.Tables.STREAM_DATA;
import static uk.co.spotistats.generated.tables.StreamingData.STREAMING_DATA;
import static uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUpsertResult.Builder.aStreamingDataInsertResult;
import static uk.co.spotistats.spotistatsservice.Domain.StreamingData.Builder.aStreamingData;

@Component
public class StreamingDataUploadRepository {

    private final DSLContext db;

    public StreamingDataUploadRepository(DSLContext db) {
        this.db = db;
    }

    public Optional<StreamingData> getStreamingDataByUsername(String username) {
        Optional<uk.co.spotistats.generated.tables.pojos.StreamingData> streamingDataEntity =
                Optional.ofNullable(db.selectFrom(STREAMING_DATA).where(STREAMING_DATA.USERNAME.eq(username))
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class));

        return streamingDataEntity.map(this::mapStreamingDataEntityToStreamingData);
    }

    public Optional<StreamingDataUpsertResult> insertStreamingData(StreamingData streamingData, String username) {
        uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity = db.insertInto(STREAMING_DATA)
                .set(STREAMING_DATA.USERNAME, username)
                .set(STREAMING_DATA.STREAM_COUNT, streamingData.streamCount())
                .set(STREAMING_DATA.FIRST_STREAM_DATE, streamingData.firstStreamDateTime())
                .set(STREAMING_DATA.LAST_STREAM_DATA, streamingData.lastStreamDateTime())
                .returning().fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class);

        return Optional.ofNullable(streamingDataEntity).map(this::mapStreamingDataEntityToStreamingDataInsertResult);
    }

    @Async
    public void insertStreamData(StreamData streamData, String username) {
        db.insertInto(STREAM_DATA)
                .set(STREAM_DATA.USERNAME, username)
                .set(STREAM_DATA.TIME_STREAMED, (int) streamData.timeStreamed())
                .set(STREAM_DATA.ALBUM_NAME, streamData.album())
                .set(STREAM_DATA.ARTIST_NAME, streamData.artist())
                .set(STREAM_DATA.DATE, streamData.streamDateTime().toLocalDate())
                .set(STREAM_DATA.DATE_TIME, streamData.streamDateTime())
                .set(STREAM_DATA.TRACK_URI, streamData.trackUri())
                .set(STREAM_DATA.TRACK_NAME, streamData.name())
                .set(STREAM_DATA.PLATFORM, streamData.platform())
                .set(STREAM_DATA.COUNTRY, streamData.country()).execute();
    }

    public Optional<StreamingDataUpsertResult> updateStreamingData(StreamingData streamingData, String username) {
        uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity = db.update(STREAMING_DATA)
                .set(STREAMING_DATA.FIRST_STREAM_DATE, streamingData.firstStreamDateTime())
                .set(STREAMING_DATA.LAST_STREAM_DATA, streamingData.lastStreamDateTime())
                .set(STREAMING_DATA.STREAM_COUNT, streamingData.streamCount())
                .where(STREAMING_DATA.USERNAME.eq(username))
                .returning().fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class);

        return Optional.ofNullable(streamingDataEntity).map(this::mapStreamingDataEntityToStreamingDataInsertResult);
    }

    private StreamingDataUpsertResult mapStreamingDataEntityToStreamingDataInsertResult(uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity) {
        return aStreamingDataInsertResult()
                .withUsername(streamingDataEntity.getUsername())
                .withFirstStreamDateTime(streamingDataEntity.getFirstStreamDate())
                .withLastStreamDateTime(streamingDataEntity.getLastStreamData())
                .withTotalStreams(streamingDataEntity.getStreamCount())
                .build();
    }

    private StreamingData mapStreamingDataEntityToStreamingData(uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity) {
        return aStreamingData()
                .withFirstStreamDateTime(streamingDataEntity.getFirstStreamDate())
                .withLastStreamDateTime(streamingDataEntity.getLastStreamData())
                .withTotalStreams(streamingDataEntity.getStreamCount())
                .build();
    }
}
