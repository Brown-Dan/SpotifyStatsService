package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.co.spotistats.spotistatsservice.Controller.Mapper.MultipartFileToStreamingDataMapper;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
import uk.co.spotistats.spotistatsservice.Domain.Response.StreamingDataUploadResponse;
import uk.co.spotistats.spotistatsservice.Domain.StreamingData;
import uk.co.spotistats.spotistatsservice.Service.FileUploadService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper;

    public FileUploadController(FileUploadService fileUploadService, MultipartFileToStreamingDataMapper multipartFileToStreamingDataMapper) {
        this.fileUploadService = fileUploadService;
        this.multipartFileToStreamingDataMapper = multipartFileToStreamingDataMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<StreamingDataUploadResponse, Error>> upload(@RequestParam Map<String, MultipartFile> files) {

        Result<StreamingData, Error> streamingDataResult = multipartFileToStreamingDataMapper.map(files.values().stream().toList().getFirst());

        if (streamingDataResult.isFailure()) {
            return badRequest(streamingDataResult.getError());
        }

        List<Result<StreamingData, Error>> mappedFiles = files.values().stream().map(multipartFileToStreamingDataMapper::map).toList();

        List<Error> errors = mappedFiles.stream().filter(Result::isFailure).map(Result::getError).toList();

        if (!errors.isEmpty()){
            return badRequest(mappedFiles.getFirst().getError());
        }

        Result<StreamingDataUploadResponse, Error> streamingDataUploadResponseResult =
                fileUploadService.uploadFile(streamingDataResult.getValue());

        return switch (streamingDataUploadResponseResult) {
            case Result.Success(StreamingDataUploadResponse streamingDataUploadResponse) ->
                    ok(streamingDataUploadResponse);
            case Result.Failure(Error error) -> badRequest(error);
        };
    }


    private <T> ResponseEntity<ApiResult<T, Error>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Error>> badRequest(Error error) {
        return new ResponseEntity<>(ApiResult.failure(error), HttpStatus.BAD_REQUEST);
    }
}
