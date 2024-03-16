package uk.co.spotistats.spotistatsservice.Service.Mapper;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.RankedTrackDataResource;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.TopTracksResource;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.UnrankedTrackDataResource;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.RankedTrackDataResource.Builder.aRankedStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.RankedTopTracksResource.Builder.aRankedTopTracksResponse;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.UnrankedTopTracksResource.Builder.anUnrankedTopTracksResponse;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.UnrankedTrackDataResource.Builder.anUnrankedTrackDataResource;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

@Component
public class StreamingDataToTopTracksMapper {

    private final StreamingDataRepository streamingDataRepository;

    public StreamingDataToTopTracksMapper(StreamingDataRepository streamingDataRepository) {
        this.streamingDataRepository = streamingDataRepository;
    }

    public Result<TopTracksResource, Errors> mapToRanked(StreamingData streamingData, TopTracksSearchRequest searchRequest) {
        Result<StreamingData, Error> getStreamingDataResult = streamingDataRepository.getStreamingData(searchRequest.userId());
        if (getStreamingDataResult.isFailure()) {
            return new Result.Failure<>(Errors.fromError(getStreamingDataResult.getError()));
        }
        StreamingDataSearchRequest.Builder streamingDataSearchRequestBuilder = aStreamingDataSearchRequest()
                .withUsername(searchRequest.userId())
                .withOrderBy(StreamDataSearchRequestOrderBy.DATE.name())
                .withStartDate(getStreamingDataResult.getValue().firstStreamDateTime().toLocalDate())
                .withEndDate(getStreamingDataResult.getValue().lastStreamDateTime().toLocalDate());

        List<RankedTrackDataResource> rankedTrackDataResources = streamingData.streamData().stream().map(streamData ->
                mapRankedTrackData(streamData, streamingDataSearchRequestBuilder,
                        calculateRank(streamingData.streamData().indexOf(streamData), searchRequest.page(), searchRequest.limit()))).toList();

        return new Result.Success<>(aRankedTopTracksResponse()
                .withTracks(rankedTrackDataResources)
                .withTotalResults(100)
                .withPage(searchRequest.page())
                .build());
    }

    private Integer calculateRank(Integer index, Integer page, Integer limit){
        return (index + 1) + (page * limit);
    }

    public Result<TopTracksResource, Errors> mapToUnranked(StreamingData streamingData, TopTracksSearchRequest searchRequest) {
        return new Result.Success<>(anUnrankedTopTracksResponse()
                .withStreamData(streamingData.streamData().stream().map(streamData -> mapUnrankedTrackData(streamData,
                        calculateRank(streamingData.streamData().indexOf(streamData), searchRequest.page(), searchRequest.limit()))).toList())
                .withTotalResults(100)
                .withPage(searchRequest.page())
                .build());
    }

    private UnrankedTrackDataResource mapUnrankedTrackData(StreamData streamData, Integer rank) {
        return anUnrankedTrackDataResource()
                .withAlbum(streamData.album())
                .withArtist(streamData.artist())
                .withLengthMs(streamData.timeStreamed())
                .withRank(rank)
                .withTrackUri(streamData.trackUri())
                .withName(streamData.name())
                .build();
    }

    private RankedTrackDataResource mapRankedTrackData(StreamData streamData, StreamingDataSearchRequest.Builder searchRequestBuilder, Integer rank) {
        StreamingDataSearchRequest streamingDataSearchRequest = searchRequestBuilder.withUri(streamData.trackUri()).build();
        StreamingData streamingData = streamingDataRepository.search(streamingDataSearchRequest);

        long totalTimeStreamed = streamingData.streamData().stream().mapToLong(StreamData::timeStreamed).sum();
        RankedTrackDataResource.Builder rankedStreamData = aRankedStreamData()
                .withTotalMsPlayed((int) totalTimeStreamed)
                .withTrackName(streamData.name())
                .withRanking(rank)
                .withTotalMinutesPlayed(((int) totalTimeStreamed / 1000) / 60)
                .withArtistName(streamData.artist())
                .withAlbumName(streamData.album())
                .withTrackUri(streamData.trackUri())
                .withTotalStreams(streamingData.size());

        return streamingData.streamData().isEmpty() ? rankedStreamData.build() :
                rankedStreamData.withLastStreamedDate(Optional.ofNullable(streamingData.streamData().getLast())
                        .map(StreamData::streamDateTime).orElse(null)).build();
    }
}
