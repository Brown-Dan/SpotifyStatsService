package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Request.StreamDataSearchRequestOrderBy;

import java.util.ArrayList;
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

    public Result<StreamingData, Error> getStreamingData (String username){
        uk.co.spotistats.generated.tables.pojos.StreamingData streamingData =
                db.selectFrom(STREAMING_DATA).where(STREAMING_DATA.USERNAME.eq(username))
                        .fetchOneInto(uk.co.spotistats.generated.tables.pojos.StreamingData.class);
        if (streamingData == null){
            return new Result.Failure<>(new Error("Streaming data not found for user - %s".formatted(username)));
        }
        return new Result.Success<>(StreamingData.fromStreamingDataEntity(streamingData));
    }

    public StreamingData search(StreamingDataSearchRequest streamingDataSearchRequest, String username) {
        List<Condition> conditions = buildQueryConditions(streamingDataSearchRequest);

        List<uk.co.spotistats.generated.tables.pojos.StreamData> streamData =
                db.selectFrom(STREAM_DATA).where(conditions).and(STREAM_DATA.USERNAME.eq(username))
                        .orderBy(StreamDataSearchRequestOrderBy.valueOf(streamingDataSearchRequest.orderBy()).getField()).fetchInto(uk.co.spotistats.generated.tables.pojos.StreamData.class);

        return buildStreamingData(streamData);
    }

    public StreamingData search(StreamingDataSearchRequest streamingDataSearchRequest) {
        return search(streamingDataSearchRequest, streamingDataSearchRequest.username());
    }

    private List<Condition> buildQueryConditions(StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Condition> conditions = new ArrayList<>();

        if (streamingDataSearchRequest.country() != null) {
            conditions.add(STREAM_DATA.COUNTRY.eq(streamingDataSearchRequest.country()));
        }
        if (streamingDataSearchRequest.on() != null) {
            conditions.add(STREAM_DATA.DATE.eq(streamingDataSearchRequest.on()));
        } else {
            conditions.add(STREAM_DATA.DATE.between(streamingDataSearchRequest.start(), streamingDataSearchRequest.end()));
        }
        if (streamingDataSearchRequest.uri() != null) {
            conditions.add(STREAM_DATA.TRACK_URI.eq(streamingDataSearchRequest.uri()));
        }
        if (streamingDataSearchRequest.album() != null) {
            conditions.add(STREAM_DATA.ALBUM_NAME.eq(streamingDataSearchRequest.album()));
        }
        if (streamingDataSearchRequest.artist() != null) {
            conditions.add(STREAM_DATA.ARTIST_NAME.eq(streamingDataSearchRequest.artist()));
        }
        if (streamingDataSearchRequest.trackName() != null) {
            conditions.add(STREAM_DATA.TRACK_NAME.eq(streamingDataSearchRequest.trackName()));
        }
        if (streamingDataSearchRequest.platform() != null) {
            conditions.add(STREAM_DATA.PLATFORM.eq(streamingDataSearchRequest.platform()));
        }

        return conditions;
    }

    private StreamingData buildStreamingData(List<uk.co.spotistats.generated.tables.pojos.StreamData> streamData) {
        if (streamData.isEmpty()) {
            return aStreamingData()
                    .withTotalStreams(0)
                    .withStreamData(new ArrayList<>())
                    .build();
        }
        return aStreamingData()
                .withFirstStreamDateTime(streamData.getFirst().getDateTime())
                .withStreamData(streamData.stream().map(this::mapStreamDataEntityToStreamData).toList())
                .withLastStreamDateTime(streamData.getLast().getDateTime())
                .withTotalStreams(streamData.size())
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