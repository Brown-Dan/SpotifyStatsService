package uk.co.spotistats.spotistatsservice.Repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import uk.co.spotistats.generated.tables.pojos.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.StreamingDataSearchRequest;

import java.util.ArrayList;
import java.util.List;

import static uk.co.spotistats.generated.tables.StreamData.STREAM_DATA;
import static uk.co.spotistats.spotistatsservice.Domain.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.StreamingData.Builder.aStreamingData;

@Repository
public class StreamingDataRepository {

    private final DSLContext db;

    public StreamingDataRepository(DSLContext db) {
        this.db = db;
    }

    public StreamingData get(StreamingDataSearchRequest streamingDataSearchRequest){
        List<Condition> conditions = buildQueryConditions(streamingDataSearchRequest);

        List<StreamData> streamData =
                db.selectFrom(STREAM_DATA).where(conditions).orderBy(STREAM_DATA.DATE_TIME).fetchInto(StreamData.class);

        return buildStreamingData(streamData);
    }

    private List<Condition> buildQueryConditions(StreamingDataSearchRequest streamingDataSearchRequest) {
        List<Condition> conditions = new ArrayList<>();

        if (streamingDataSearchRequest.country() != null) {
            conditions.add(STREAM_DATA.COUNTRY.eq(streamingDataSearchRequest.country()));
        }
        if (streamingDataSearchRequest.on() != null){
            conditions.add(STREAM_DATA.DATE.eq(streamingDataSearchRequest.on()));
        } else{
            conditions.add(STREAM_DATA.DATE.between(streamingDataSearchRequest.start(), streamingDataSearchRequest.end()));
        }
        if (streamingDataSearchRequest.uri() != null){
            conditions.add(STREAM_DATA.TRACK_URI.eq(streamingDataSearchRequest.uri()));
        }
        if (streamingDataSearchRequest.album() != null){
            conditions.add(STREAM_DATA.ALBUM_NAME.eq(streamingDataSearchRequest.album()));
        }
        if (streamingDataSearchRequest.artist() != null){
            conditions.add(STREAM_DATA.ARTIST_NAME.eq(streamingDataSearchRequest.artist()));
        }
        if (streamingDataSearchRequest.trackName() != null){
            conditions.add(STREAM_DATA.TRACK_NAME.eq(streamingDataSearchRequest.trackName()));
        }
        if (streamingDataSearchRequest.platform() != null){
            conditions.add(STREAM_DATA.PLATFORM.eq(streamingDataSearchRequest.platform()));
        }

        return conditions;
    }

    private StreamingData buildStreamingData(List<StreamData> streamData) {
        if (streamData.isEmpty()){
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

    private uk.co.spotistats.spotistatsservice.Domain.StreamData mapStreamDataEntityToStreamData(StreamData streamData){
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
