package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.ConditionBuilder;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponseTrack;

import java.util.Arrays;
import java.util.List;

import static uk.co.spotistats.generated.tables.StreamData.STREAM_DATA;
import static uk.co.spotistats.generated.tables.StreamingData.STREAMING_DATA;
import static uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse.Builder.aSearchResponse;
import static uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponseTrack.Builder.aSearchResponseTrack;

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

    public SearchResponse search(StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Condition> conditions = buildQueryConditions(streamingDataSearchRequest);

        List<uk.co.spotistats.generated.tables.pojos.StreamData> streamData =
                db.selectFrom(STREAM_DATA).where(conditions).and(STREAM_DATA.USERNAME.eq(streamingDataSearchRequest.userId()))
                        .orderBy(StreamDataSearchRequestOrderBy.valueOf(streamingDataSearchRequest.orderBy()).getField())
                        .limit(streamingDataSearchRequest.limit()).fetchInto(uk.co.spotistats.generated.tables.pojos.StreamData.class);
        return mapStreamDataEntityListToSearchResponse(streamData);
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

    private SearchResponse mapStreamDataEntityListToSearchResponse(List<uk.co.spotistats.generated.tables.pojos.StreamData> streamData) {
        if (streamData.isEmpty()) {
            return aSearchResponse()
                    .withTracks(List.of())
                    .build();
        }
        return aSearchResponse()
                .withTracks(streamData.stream().map(this::mapStreamDataEntityToSearchResponseTrack).toList())
                .withFirstStreamDate(streamData.getFirst().getDateTime())
                .withLastStreamDate(streamData.getLast().getDateTime())
                .withSize(streamData.size())
                .withTotalStreamTimeMinutes(((int) streamData.stream().mapToLong(uk.co.spotistats.generated.tables.pojos.StreamData::getTimeStreamed).sum() / 1000) / 60)
                .build();
    }

    private SearchResponseTrack mapStreamDataEntityToSearchResponseTrack(uk.co.spotistats.generated.tables.pojos.StreamData streamData) {
        return aSearchResponseTrack()
                .withTrackName(streamData.getTrackName())
                .withTotalMsPlayed(streamData.getTimeStreamed())
                .withArtistName(streamData.getArtistName())
                .withAlbumName(streamData.getAlbumName())
                .withPlatform(streamData.getPlatform())
                .withTrackUri(streamData.getTrackUri())
                .withStreamDateTime(streamData.getDateTime())
                .withCountry(streamData.getCountry())
                .build();
    }
}
