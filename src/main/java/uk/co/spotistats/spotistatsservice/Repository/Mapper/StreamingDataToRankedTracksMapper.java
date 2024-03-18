package uk.co.spotistats.spotistatsservice.Repository.Mapper;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Request.TopTracksSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponseTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Advanced.AdvancedRankedTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Simple.SimpleRankedTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.TopTracks;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import java.util.List;
import java.util.Optional;

import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Advanced.AdvancedRankedTrack.Builder.anAdvancedRankedTrack;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Advanced.AdvancedRankedTracks.Builder.someAdvancedRankedTracks;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Simple.SimpleRankedTrack.Builder.aSimpleRankedTrack;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopTracks.Simple.SimpleRankedTracks.Builder.someSimpleRankedTracks;

@Component
public class StreamingDataToRankedTracksMapper {

    private final StreamingDataRepository streamingDataRepository;

    public StreamingDataToRankedTracksMapper(StreamingDataRepository streamingDataRepository) {
        this.streamingDataRepository = streamingDataRepository;
    }

    public Result<TopTracks, Errors> mapToAdvanced(StreamingData streamingData, TopTracksSearchRequest searchRequest, String playlistId) {
        Result<StreamingData, Error> getStreamingDataResult = streamingDataRepository.getStreamingData(searchRequest.userId());
        if (getStreamingDataResult.isFailure()) {
            return new Result.Failure<>(Errors.fromError(getStreamingDataResult.getError()));
        }
        StreamingDataSearchRequest.Builder streamingDataSearchRequestBuilder = aStreamingDataSearchRequest()
                .withUserId(searchRequest.userId())
                .withOrderBy(StreamDataSearchRequestOrderBy.DATE_ASC.name())
                .withStartDate(getStreamingDataResult.getValue().firstStreamDateTime().toLocalDate())
                .withEndDate(getStreamingDataResult.getValue().lastStreamDateTime().toLocalDate());

        List<AdvancedRankedTrack> advancedRankedTracks = streamingData.streamData().stream().map(streamData ->
                mapAdvancedTrackData(streamData, streamingDataSearchRequestBuilder,
                        calculateRank(streamingData.streamData().indexOf(streamData), searchRequest.page(), searchRequest.limit()))).toList();

        return new Result.Success<>(someAdvancedRankedTracks()
                .withTracks(advancedRankedTracks)
                .withTotalResults(100)
                .withPage(searchRequest.page())
                .withCreatedPlaylist(playlistId != null)
                .withPlaylistId(playlistId)
                .build());
    }
    public Result<TopTracks, Errors> mapToSimple(StreamingData streamingData, TopTracksSearchRequest searchRequest, String playlistId) {
        return new Result.Success<>(someSimpleRankedTracks()
                .withTracks(streamingData.streamData().stream().map(streamData -> mapSimpleTrackData(streamData,
                        calculateRank(streamingData.streamData().indexOf(streamData), searchRequest.page(), searchRequest.limit()))).toList())
                .withTotalResults(100)
                .withPage(searchRequest.page())
                .withCreatedPlaylist(playlistId != null)
                .withPlaylistId(playlistId)
                .build());
    }

    private SimpleRankedTrack mapSimpleTrackData(StreamData streamData, Integer rank) {
        return aSimpleRankedTrack()
                .withAlbum(streamData.album())
                .withImage(streamData.image())
                .withArtist(streamData.artist())
                .withLengthMs(streamData.timeStreamed())
                .withRank(rank)
                .withTrackUri(streamData.trackUri())
                .withName(streamData.name())
                .build();
    }

    private AdvancedRankedTrack mapAdvancedTrackData(StreamData streamData, StreamingDataSearchRequest.Builder searchRequestBuilder, Integer rank) {
        StreamingDataSearchRequest streamingDataSearchRequest = searchRequestBuilder.withUri(streamData.trackUri()).build();
        SearchResponse searchResponse = streamingDataRepository.search(streamingDataSearchRequest);

        long totalTimeStreamed = searchResponse.tracks().stream().mapToLong(SearchResponseTrack::totalMsPlayed).sum();
        AdvancedRankedTrack.Builder advancedTrackData = anAdvancedRankedTrack()
                .withTotalMsPlayed((int) totalTimeStreamed)
                .withTrackName(streamData.name())
                .withRanking(rank)
                .withImageUrl(streamData.image())
                .withTotalMinutesPlayed(((int) totalTimeStreamed / 1000) / 60)
                .withArtistName(streamData.artist())
                .withAlbumName(streamData.album())
                .withTrackUri(streamData.trackUri())
                .withTotalStreams(searchResponse.size());

        return searchResponse.tracks().isEmpty() ? advancedTrackData.build() :
                advancedTrackData.withLastStreamedDate(Optional.ofNullable(searchResponse.tracks().getLast())
                                .map(SearchResponseTrack::streamDateTime).orElse(null)).
                        withFirstStreamedDate(Optional.ofNullable(searchResponse.tracks().getFirst())
                                .map(SearchResponseTrack::streamDateTime).orElse(null))
                        .build();
    }

    private Integer calculateRank(Integer index, Integer page, Integer limit) {
        return (index + 1) + ((page - 1) * limit);
    }

}
