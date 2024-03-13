package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Response.Result;
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
        Result<SpotifyAuthData, Errors> result = spotifyAuthService.insertSpotifyAuthData(spotifyAuthData);

        if (result.isFailure()) {
            return badRequest(result.getError());
        }
        return ok(result.getValue());
    }

    @GetMapping(value = "/authenticate/callback")
    public ResponseEntity<ApiResult<SpotifyAuthData, Errors>> authenticationCallback(@RequestParam String state, @RequestParam String code) {
        Result<SpotifyAuthData, Errors> result = spotifyAuthService.exchangeAccessToken(state, code);

        if (result.isFailure()) {
            return badRequest(result.getError());
        }
        return ok(result.getValue());
    }

    @GetMapping(value = "/authorize")
    public ResponseEntity<ApiResult<String, Errors>> authorize(@RequestParam String username) {
        spotifyAuthService.authorize(username);
        return ok("Success");
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> badRequest(Errors errors) {
        return new ResponseEntity<>(ApiResult.failure(errors), errors.httpStatus());
    }
}
