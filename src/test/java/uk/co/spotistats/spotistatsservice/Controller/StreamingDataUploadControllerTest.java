package uk.co.spotistats.spotistatsservice.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Controller.Mapper.MultipartFileToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.Upload.StreamingDataUpsertResult;
import uk.co.spotistats.spotistatsservice.Service.StreamingDataUploadService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.co.spotistats.spotistatsservice.Domain.Model.StreamingData.Builder.someStreamingData;
import static uk.co.spotistats.spotistatsservice.Domain.Response.Upload.StreamingDataUpsertResult.Builder.aStreamingDataInsertResult;

@ExtendWith(MockitoExtension.class)
public class StreamingDataUploadControllerTest {

    @Mock
    private StreamingDataUploadService streamingDataUploadService;

    @Mock
    private MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    @Mock
    private MultipartFile multipartFile;

    private StreamingDataUploadController streamingDataUploadController;

    private static final String USER_ID = "userId";

    @BeforeEach
    void setUp() {
        streamingDataUploadController = new StreamingDataUploadController(streamingDataUploadService, multipartFileToStreamingDataMapper);
    }

    @Test
    void upload_givenMultipartFile_shouldMapToStreamingData_andCallRepository() throws IOException {
        StreamingData streamingData = someStreamingData().build();
        StreamingDataUpsertResult streamingDataUpsertResult = aStreamingDataInsertResult().build();

        when(multipartFileToStreamingDataMapper.map(any())).thenReturn(new Result.Success<>(streamingData));
        when(streamingDataUploadService.upsert(any(), any())).thenReturn(new Result.Success<>(streamingDataUpsertResult));

        ResponseEntity<ApiResult<StreamingDataUpsertResult, Errors>> result = streamingDataUploadController.upload(multipartFile, USER_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>(streamingDataUpsertResult));

        verify(multipartFileToStreamingDataMapper).map(multipartFile);
        verify(streamingDataUploadService).upsert(streamingData, USER_ID);
    }

    @Test
    void upload_givenMultipartFile_givenErrorWhenMapping_shouldHandleGracefully() throws IOException {
        Error expectedError = Error.failedToParseData("streamingDataFile", "failed to read streaming data");

        when(multipartFileToStreamingDataMapper.map(any())).thenReturn(new Result.Failure<>(expectedError));

        ResponseEntity<ApiResult<StreamingDataUpsertResult, Errors>> result = streamingDataUploadController.upload(multipartFile, USER_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(Errors.fromError(expectedError)));

        verify(multipartFileToStreamingDataMapper).map(multipartFile);
        verifyNoInteractions(streamingDataUploadService);
    }
}
