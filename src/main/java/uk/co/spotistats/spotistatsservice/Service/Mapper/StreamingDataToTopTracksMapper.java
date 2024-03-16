package uk.co.spotistats.spotistatsservice.Service.Mapper;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Advanced.AdvancedTrackDataResource;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.TopTracksResource;
import uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Simple.SimpleTrackDataResource;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Advanced.AdvancedTrackDataResource.Builder.anAdvancedTrackDataResource;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Advanced.AdvancedTopTracksResource.Builder.anAdvancedTopTracksResource;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Simple.SimpleTopTracksResource.Builder.aSimpleTopTracksResponse;
import static uk.co.spotistats.spotistatsservice.Domain.Model.TopTracks.Simple.SimpleTrackDataResource.Builder.aSimpleTrackDataResource;
import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;

@Component
public class StreamingDataToTopTracksMapper {

    private final StreamingDataRepository streamingDataRepository;

    public StreamingDataToTopTracksMapper(StreamingDataRepository streamingDataRepository) {
        this.streamingDataRepository = streamingDataRepository;
    }

    public Result<TopTracksResource, Errors> mapToAdvanced(StreamingData streamingData, TopTracksSearchRequest searchRequest) {
        Result<StreamingData, Error> getStreamingDataResult = streamingDataRepository.getStreamingData(searchRequest.userId());
        if (getStreamingDataResult.isFailure()) {
            return new Result.Failure<>(Errors.fromError(getStreamingDataResult.getError()));
        }
        StreamingDataSearchRequest.Builder streamingDataSearchRequestBuilder = aStreamingDataSearchRequest()
                .withUsername(searchRequest.userId())
                .withOrderBy(StreamDataSearchRequestOrderBy.DATE.name())
                .withStartDate(getStreamingDataResult.getValue().firstStreamDateTime().toLocalDate())
                .withEndDate(getStreamingDataResult.getValue().lastStreamDateTime().toLocalDate());

        List<AdvancedTrackDataResource> advancedTrackDataResources = streamingData.streamData().stream().map(streamData ->
                mapAdvancedTrackData(streamData, streamingDataSearchRequestBuilder,
                        calculateRank(streamingData.streamData().indexOf(streamData), searchRequest.page(), searchRequest.limit()))).toList();

        return new Result.Success<>(anAdvancedTopTracksResource()
                .withTracks(advancedTrackDataResources)
                .withTotalResults(100)
                .withPage(searchRequest.page())
                .build());
    }

    private Integer calculateRank(Integer index, Integer page, Integer limit){
        return (index + 1) + ((page - 1) * limit);
    }

    public Result<TopTracksResource, Errors> mapToSimple(StreamingData streamingData, TopTracksSearchRequest searchRequest) {
        return new Result.Success<>(aSimpleTopTracksResponse()
                .withStreamData(streamingData.streamData().stream().map(streamData -> mapSimpleTrackData(streamData,
                        calculateRank(streamingData.streamData().indexOf(streamData), searchRequest.page(), searchRequest.limit()))).toList())
                .withTotalResults(100)
                .withPage(searchRequest.page())
                .build());
    }

    private SimpleTrackDataResource mapSimpleTrackData(StreamData streamData, Integer rank) {
        return aSimpleTrackDataResource()
                .withAlbum(streamData.album())
                .withArtist(streamData.artist())
                .withLengthMs(streamData.timeStreamed())
                .withRank(rank)
                .withTrackUri(streamData.trackUri())
                .withName(streamData.name())
                .build();
    }

    private AdvancedTrackDataResource mapAdvancedTrackData(StreamData streamData, StreamingDataSearchRequest.Builder searchRequestBuilder, Integer rank) {
        StreamingDataSearchRequest streamingDataSearchRequest = searchRequestBuilder.withUri(streamData.trackUri()).build();
        StreamingData streamingData = streamingDataRepository.search(streamingDataSearchRequest);

        long totalTimeStreamed = streamingData.streamData().stream().mapToLong(StreamData::timeStreamed).sum();
        AdvancedTrackDataResource.Builder advancedTrackData = anAdvancedTrackDataResource()
                .withTotalMsPlayed((int) totalTimeStreamed)
                .withTrackName(streamData.name())
                .withRanking(rank)
                .withTotalMinutesPlayed(((int) totalTimeStreamed / 1000) / 60)
                .withArtistName(streamData.artist())
                .withAlbumName(streamData.album())
                .withTrackUri(streamData.trackUri())
                .withTotalStreams(streamingData.size());

        return streamingData.streamData().isEmpty() ? advancedTrackData.build() :
                advancedTrackData.withLastStreamedDate(Optional.ofNullable(streamingData.streamData().getLast())
                        .map(StreamData::streamDateTime).orElse(null)).build();
    }
}
