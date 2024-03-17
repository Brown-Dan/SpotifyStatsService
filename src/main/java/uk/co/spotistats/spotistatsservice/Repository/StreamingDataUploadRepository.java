package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Response.Upload.StreamingDataUpsertResult;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static uk.co.spotistats.generated.Tables.STREAM_DATA;
import static uk.co.spotistats.generated.tables.StreamingData.STREAMING_DATA;
import static uk.co.spotistats.spotistatsservice.Domain.Response.Upload.StreamingDataUpsertResult.Builder.aStreamingDataInsertResult;

@Component
public class StreamingDataUploadRepository {

    private final DSLContext db;

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataUploadRepository.class);
    private final static Map<Integer, Short> javaToSqlDayOfTheWeek = Map.of(
            7, (short) 0,
            6, (short) 5,
            5, (short) 4,
            4, (short) 3,
            3, (short) 2,
            2, (short) 1,
            1, (short) 0
    );

    public StreamingDataUploadRepository(DSLContext db) {
        this.db = db;
    }

    public Optional<StreamingData> getStreamingDataByUsername(String username) {
        Optional<uk.co.spotistats.generated.tables.pojos.StreamingData> streamingDataEntity =
                Optional.ofNullable(db.selectFrom(STREAMING_DATA).where(STREAMING_DATA.USERNAME.eq(username))
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class));

        return streamingDataEntity.map(StreamingData::fromStreamingDataEntity);
    }

    public Optional<StreamingDataUpsertResult> insertStreamingData(StreamingData streamingData, String username) {
        uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity = db.insertInto(STREAMING_DATA)
                .set(STREAMING_DATA.USERNAME, username)
                .set(STREAMING_DATA.STREAM_COUNT, streamingData.size())
                .set(STREAMING_DATA.FIRST_STREAM_DATE, streamingData.firstStreamDateTime())
                .set(STREAMING_DATA.LAST_STREAM_DATA, streamingData.lastStreamDateTime())
                .set(STREAMING_DATA.LAST_UPDATED, streamingData.lastUpdated())
                .returning().fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class);

        return Optional.ofNullable(streamingDataEntity).map(this::mapStreamingDataEntityToStreamingDataInsertResult);
    }

    @Async
    public void insertStreamData(StreamData streamData, String username) {
        LOG.info("Inserting stream data async for user - {}", username);
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
                .set(STREAM_DATA.TIME, streamData.streamDateTime().toLocalTime())
                .set(STREAM_DATA.DAY_OF_THE_WEEK, javaToSqlDayOfTheWeek.get(streamData.streamDateTime().getDayOfWeek().getValue()))
                .set(STREAM_DATA.MONTH, (short) streamData.streamDateTime().getMonthValue())
                .set(STREAM_DATA.YEAR, (short) streamData.streamDateTime().getYear())
                .set(STREAM_DATA.COUNTRY, streamData.country()).execute();
    }

    public Optional<StreamingDataUpsertResult> updateStreamingData(StreamingData streamingData, String username) {
        uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity = db.update(STREAMING_DATA)
                .set(STREAMING_DATA.FIRST_STREAM_DATE, streamingData.firstStreamDateTime())
                .set(STREAMING_DATA.LAST_STREAM_DATA, streamingData.lastStreamDateTime())
                .set(STREAMING_DATA.STREAM_COUNT, streamingData.size())
                .set(STREAMING_DATA.LAST_UPDATED, LocalDateTime.now())
                .where(STREAMING_DATA.USERNAME.eq(username))
                .returning().fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class);

        return Optional.ofNullable(streamingDataEntity).map(this::mapStreamingDataEntityToStreamingDataInsertResult);
    }

    private StreamingDataUpsertResult mapStreamingDataEntityToStreamingDataInsertResult(uk.co.spotistats.generated.tables.pojos.StreamingData streamingDataEntity) {
        return aStreamingDataInsertResult()
                .withUsername(streamingDataEntity.getUsername())
                .withFirstStreamDateTime(streamingDataEntity.getFirstStreamDate())
                .withLastStreamDateTime(streamingDataEntity.getLastStreamData())
                .withSize(streamingDataEntity.getStreamCount())
                .build();
    }
}
