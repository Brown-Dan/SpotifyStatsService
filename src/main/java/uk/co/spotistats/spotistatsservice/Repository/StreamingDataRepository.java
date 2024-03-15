package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.ConditionBuilder;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.co.spotistats.generated.tables.StreamData.STREAM_DATA;
import static uk.co.spotistats.generated.tables.StreamingData.STREAMING_DATA;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.aStreamingData;

@Repository
public class StreamingDataRepository {

    private final DSLContext db;

    public StreamingDataRepository(DSLContext db) {
        this.db = db;
    }

    public Result<StreamingData, Error> getStreamingData(String username) {
        uk.co.spotistats.generated.tables.pojos.StreamingData streamingData =
                db.selectFrom(STREAMING_DATA).where(STREAMING_DATA.USERNAME.eq(username))
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class);
        if (streamingData == null) {
            return new Result.Failure<>(Error.notFound("streamingData", username));
        }
        return new Result.Success<>(StreamingData.fromStreamingDataEntity(streamingData));
    }

    public StreamingData search(StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Condition> conditions = buildQueryConditions(streamingDataSearchRequest);

        List<uk.co.spotistats.generated.tables.pojos.StreamData> streamData =
                db.selectFrom(STREAM_DATA).where(conditions).and(STREAM_DATA.USERNAME.eq(streamingDataSearchRequest.username()))
                        .orderBy(StreamDataSearchRequestOrderBy.valueOf(streamingDataSearchRequest.orderBy()).getField())
                        .limit(streamingDataSearchRequest.limit()).fetchInto(uk.co.spotistats.generated.tables.pojos.StreamData.class);
        return buildStreamingData(streamData);
    }

    public List<StreamingData> getUnsyncedStreamingData() {
        return db.selectFrom(STREAMING_DATA).fetchInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class).stream()
                .map(StreamingData::fromStreamingDataEntity).filter(StreamingData::shouldSync).toList();
    }

    private List<Condition> buildQueryConditions(StreamingDataSearchRequest streamingDataSearchRequest) {
        return Arrays.stream(ConditionBuilder.values())
                .filter(condition -> condition.getGetter().apply(streamingDataSearchRequest) != null)
                .map(condition -> condition.getCondition().apply(condition.getGetter().apply(streamingDataSearchRequest)))
                .toList();
    }

    private StreamingData buildStreamingData(List<uk.co.spotistats.generated.tables.pojos.StreamData> streamData) {
        if (streamData.isEmpty()) {
            return aStreamingData()
                    .withSize(0)
                    .withStreamData(new ArrayList<>())
                    .build();
        }
        return aStreamingData()
                .withFirstStreamDateTime(streamData.getFirst().getDateTime())
                .withStreamData(streamData.stream().map(this::mapStreamDataEntityToStreamData).toList())
                .withLastStreamDateTime(streamData.getLast().getDateTime())
                .withSize(streamData.size())
                .build();
    }

    private StreamData mapStreamDataEntityToStreamData(uk.co.spotistats.generated.tables.pojos.StreamData streamData) {
        return aStreamData()
                .withName(streamData.getTrackName())
                .withTimeStreamed(streamData.getTimeStreamed())
                .withArtist(streamData.getArtistName())
                .withAlbum(streamData.getAlbumName())
                .withPlatform(streamData.getPlatform())
                .withTrackUri(streamData.getTrackUri())
                .withStreamDateTime(streamData.getDateTime())
                .withCountry(streamData.getCountry())
                .build();
    }
}
