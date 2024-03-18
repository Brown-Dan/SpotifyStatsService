package uk.co.spotistats.spotistatsservice.Repository.Mapper;

import org.springframework.stereotype.Component;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamDataSearchRequestOrderBy;
import uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponse;
import uk.co.spotistats.spotistatsservice.Domain.Response.Search.SearchResponseTrack;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.AdvancedTopArtist;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.SimpleTopArtist;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.SimpleTopArtists;
import uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.TopArtists;
import uk.co.spotistats.spotistatsservice.Repository.StreamingDataRepository;

import static uk.co.spotistats.spotistatsservice.Domain.Request.Search.StreamingDataSearchRequest.Builder.aStreamingDataSearchRequest;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.AdvancedTopArtist.Builder.anAdvancedTopArtist;
import static uk.co.spotistats.spotistatsservice.Domain.Response.TopArtists.AdvancedTopArtists.Builder.someAdvancedTopArtists;

@Component
public class SimpleTopArtistsToAdvancedTopArtistsMapper {

    private final StreamingDataRepository streamingDataRepository;

    public SimpleTopArtistsToAdvancedTopArtistsMapper(StreamingDataRepository streamingDataRepository) {
        this.streamingDataRepository = streamingDataRepository;
    }

    public Result<TopArtists, Errors> map(SimpleTopArtists simpleTopArtists, String userId) {
        Result<StreamingData, Error> getStreamingDataResult = streamingDataRepository.getStreamingData(userId);
        if (getStreamingDataResult.isFailure()) {
            return new Result.Failure<>(Errors.fromError(getStreamingDataResult.getError()));
        }

        StreamingDataSearchRequest.Builder streamingDataSearchRequestBuilder = aStreamingDataSearchRequest()
                .withUserId(userId)
                .withOrderBy(StreamDataSearchRequestOrderBy.DATE_ASC.name())
                .withStartDate(getStreamingDataResult.getValue().firstStreamDateTime().toLocalDate())
                .withEndDate(getStreamingDataResult.getValue().lastStreamDateTime().toLocalDate());


        return new Result.Success<>(someAdvancedTopArtists()
                .withArtists(simpleTopArtists.artists().stream().map(simpleArtist -> mapSimpleArtistToAdvancedArtist(simpleArtist, streamingDataSearchRequestBuilder)).toList())
                .withPage(simpleTopArtists.page())
                .withTotalResults(simpleTopArtists.totalResults())
                .build());
    }

    private AdvancedTopArtist mapSimpleArtistToAdvancedArtist(SimpleTopArtist simpleTopArtist, StreamingDataSearchRequest.Builder searchRequest) {
        SearchResponse searchResponse = streamingDataRepository.search(searchRequest.withArtist(simpleTopArtist.name()).build());

        long totalMsStreamed = searchResponse.tracks().stream().mapToLong(SearchResponseTrack::totalMsPlayed).sum();

        return anAdvancedTopArtist()
                .withName(simpleTopArtist.name())
                .withImage(simpleTopArtist.image())
                .withGenres(simpleTopArtist.genres())
                .withPopularity(simpleTopArtist.popularity())
                .withSpotifyUri(simpleTopArtist.spotifyUri())
                .withImage(simpleTopArtist.image())
                .withFirstStreamedDate(searchResponse.tracks().isEmpty() ? null : searchResponse.tracks().getFirst().streamDateTime())
                .withLastStreamedDate(searchResponse.tracks().isEmpty() ? null : searchResponse.tracks().getLast().streamDateTime())
                .withTotalMsStreamed(totalMsStreamed)
                .withTotalStreams(searchResponse.tracks().size())
                .withTotalMinutesStreamed(((int) totalMsStreamed / 1000) / 60)
                .build();
    }
}
