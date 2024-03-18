package uk.co.spotistats.spotistatsservice.Controller.Mapper;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamData;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Utils.LoggerAssert;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamData.Builder.aStreamData;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.someStreamingData;
import static uk.co.spotistats.spotistatsservice.Utils.ResourceUtils.getTestResource;

@ExtendWith(MockitoExtension.class)
public class MultipartFileToStreamingDataMapperTest {

    @RegisterExtension
    final LoggerAssert loggerAssert = new LoggerAssert();

    @Mock
    private MultipartFile multipartFile;

    private MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setUp() {
        multipartFileToStreamingDataMapper = new MultipartFileToStreamingDataMapper(objectMapper);
    }

    @Test
    void map_givenMultipartFileWithValidFormat_shouldMapToStreamingData() throws IOException {
        String json = getTestResource("Requests/StreamingDataUpload.json");

        StreamingData expectedStreamingData = someStreamingData()
                .withFirstStreamDateTime(LocalDateTime.parse("2024-01-21T15:12:47"))
                .withLastStreamDateTime(LocalDateTime.parse("2024-01-21T15:12:51"))
                .withLastUpdated(null)
                .withStreamData(getExpectedStreamData())
                .withSize(3).build();

        when(multipartFile.getBytes()).thenReturn(json.getBytes());

        Result<StreamingData, Error> result = multipartFileToStreamingDataMapper.map(multipartFile);

        assertThat(result.isFailure()).isFalse();
        assertThat(result.getValue()).isEqualTo(expectedStreamingData);

        verify(multipartFile).getBytes();
    }

    @Test
    void mapGivenMultipartFileWithInvalidFormat_shouldCatchThenLogAndReturnError() throws IOException {

        when(multipartFile.getBytes()).thenReturn("this is invalid json".getBytes());
        when(multipartFile.getName()).thenReturn("Streaming data file");

        Result<StreamingData, Error> result = multipartFileToStreamingDataMapper.map(multipartFile);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Error.failedToParseData("Streaming data file", "failed to read streaming data"));
        loggerAssert.assertError("Exception accessing bytes of supplied file");

        verify(multipartFile).getBytes();
        verify(multipartFile).getName();
    }

    private List<StreamData> getExpectedStreamData() {
        StreamData streamData1 = aStreamData()
                .withName("Hard Drive Gold")
                .withArtist("alt-J")
                .withAlbum("The Dream")
                .withTrackUri("spotify:track:44OfbgdqkUVB0Cr3RFs8rl")
                .withCountry("PL")
                .withPlatform("ios")
                .withStreamDateTime(LocalDateTime.parse("2024-01-21T15:12:47"))
                .withTimeStreamed(2290)
                .build();

        StreamData streamData2 = aStreamData()
                .withName("West Coast")
                .withArtist("Lana Del Rey")
                .withAlbum("Ultraviolence")
                .withTrackUri("spotify:track:5Y6nVaayzitvsD5F7nr3DV")
                .withCountry("PL")
                .withPlatform("ios")
                .withStreamDateTime(LocalDateTime.parse("2024-01-21T15:12:50"))
                .withTimeStreamed(2150)
                .build();

        StreamData streamData3 = aStreamData()
                .withName("Ultraviolence")
                .withArtist("Lana Del Rey")
                .withAlbum("Ultraviolence")
                .withTrackUri("spotify:track:1y3r6RXiJZNBV1EI0NggpS")
                .withCountry("PL")
                .withPlatform("ios")
                .withStreamDateTime(LocalDateTime.parse("2024-01-21T15:12:51"))
                .withTimeStreamed(220)
                .build();

        return List.of(streamData1, streamData2, streamData3);
    }
}


