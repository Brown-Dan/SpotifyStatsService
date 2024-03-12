package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Service.SpotifyAuthService;

@Controller
@RequestMapping("spotify")
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<ApiResult<SpotifyAuthData, Errors>> authenticate(@RequestBody SpotifyAuthData spotifyAuthData) {
        return spotifyAuthService.insertSpotifyAuthData(spotifyAuthData).<ResponseEntity<ApiResult<SpotifyAuthData, Errors>>>
                map(error -> badRequest(Errors.fromError(error))).orElseGet(() -> ok(spotifyAuthData));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> badRequest(Errors errors) {
        return new ResponseEntity<>(ApiResult.failure(errors), errors.httpStatus());
    }
}
