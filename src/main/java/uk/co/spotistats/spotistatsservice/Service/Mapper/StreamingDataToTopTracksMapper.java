package uk.co.spotistats.spotistatsservice.Service.Mapper;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.RankedTrackData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.TopTracksResource;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.Model.RankedTrackData.Builder.aRankedStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.RankedTopTracksResource.Builder.aRankedTopTracksResponse;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.UnrankedTopTracksResource.Builder.anUnrankedTopTracksResponse;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

@Component
public class StreamingDataToTopTracksMapper {

    private final StreamingDataRepository streamingDataRepository;

    public StreamingDataToTopTracksMapper(StreamingDataRepository streamingDataRepository) {
        this.streamingDataRepository = streamingDataRepository;
    }

    public Result<TopTracksResource, Errors> map(StreamingData streamingData, String username, Integer page) {
        Result<StreamingData, Error> getStreamingDataResult = streamingDataRepository.getStreamingData(username);
        if (getStreamingDataResult.isFailure()) {
            return new Result.Failure<>(Errors.fromError(getStreamingDataResult.getError()));
        }
        StreamingDataSearchRequest.Builder streamingDataSearchRequestBuilder = aStreamingDataSearchRequest()
                .withUsername(username)
                .withOrderBy(StreamDataSearchRequestOrderBy.DATE.name())
                .withStartDate(getStreamingDataResult.getValue().firstStreamDateTime().toLocalDate())
                .withEndDate(getStreamingDataResult.getValue().lastStreamDateTime().toLocalDate());

        List<RankedTrackData> rankedTrackData = streamingData.streamData().stream().map(streamData ->
                populateRankedStreamData(streamData, streamingDataSearchRequestBuilder,
                        streamingData.streamData().indexOf(streamData))).toList();

        return new Result.Success<>(aRankedTopTracksResponse()
                .withRankedStreamData(rankedTrackData)
                .withTotalResults(100)
                .withPage(page)
                .build());
    }

    public Result<TopTracksResource, Errors> map(StreamingData streamingData, Integer page) {
        return new Result.Success<>(anUnrankedTopTracksResponse()
                .withStreamData(streamingData.streamData())
                .withTotalResults(100)
                .withPage(page)
                .build());
    }

    private RankedTrackData populateRankedStreamData(StreamData streamData, StreamingDataSearchRequest.Builder searchRequestBuilder, int rank) {
        StreamingDataSearchRequest streamingDataSearchRequest = searchRequestBuilder.withUri(streamData.trackUri()).build();
        StreamingData streamingData = streamingDataRepository.search(streamingDataSearchRequest);

        long totalTimeStreamed = streamingData.streamData().stream().mapToLong(StreamData::timeStreamed).sum();
        RankedTrackData.Builder rankedStreamData = aRankedStreamData()
                .withTotalMsPlayed((int) totalTimeStreamed)
                .withTrackName(streamData.name())
                .withRanking(rank + 1)
                .withMinutesPlayed(((int) totalTimeStreamed / 1000) / 60)
                .withArtistName(streamData.artist())
                .withAlbumName(streamData.album())
                .withTrackUri(streamData.trackUri())
                .withTotalStreams(streamingData.size());

        return streamingData.streamData().isEmpty() ? rankedStreamData.build() :
                rankedStreamData.withLastStreamedDate(Optional.ofNullable(streamingData.streamData().getLast())
                        .map(StreamData::streamDateTime).orElse(null)).build();
    }
}
