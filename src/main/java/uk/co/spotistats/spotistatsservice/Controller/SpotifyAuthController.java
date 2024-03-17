package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Service.SpotifyAuthService;

@Controller
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @GetMapping(value = "/authenticate/callback")
    public ResponseEntity<ApiResult<String, Errors>> authenticationCallback(@RequestParam String code) {
        Result<String, Errors> result = spotifyAuthService.exchangeAccessToken(code);

        if (result.isFailure()) {
            return failure(result.getError());
        }
        return ok(result.getValue());
    }

    @GetMapping(value = "/token/refresh")
    public ResponseEntity<ApiResult<String, Errors>> refreshJwtToken(@RequestHeader String jwt){
        Result<String, Errors> result = spotifyAuthService.refreshJwt(jwt);
        return switch (result){
            case Result.Success (String refreshedJwt) -> ok(refreshedJwt);
            case Result.Failure (Errors errors) -> failure(errors);
        };
    }

    @GetMapping(value = "/login")
    public RedirectView authorizationRedirect() {
        return spotifyAuthService.redirect();
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> failure(Errors errors) {
        return new ResponseEntity<>(ApiResult.failure(errors), errors.httpStatus());
    }
}
