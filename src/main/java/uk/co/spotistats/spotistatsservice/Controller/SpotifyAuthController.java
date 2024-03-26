package uk.co.spotistats.spotistatsservice.Controller;

import com.alibaba.fastjson2.JSON;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.User;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Domain.SpotifyAuth.SpotifyAuthData;
import uk.co.spotistats.spotistatsservice.Service.SpotifyAuthService;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

@Controller
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @GetMapping(value = "/authenticate/callback")
    public RedirectView authenticationCallback(@RequestParam String code) throws URISyntaxException, MalformedURLException {
        Result<String, Errors> result = spotifyAuthService.exchangeAccessToken(code);

        if (result.isFailure()) {
            return new RedirectView(new URIBuilder()
                    .setScheme("http")
                    .setHost("localhost")
                    .setPort(5173)
                    .setPath("/error")
                    .build().toURL().toString());
        }
        return new RedirectView(new URIBuilder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(5173)
                .setPath("/")
                .setParameter("jwt", result.getValue())
                .build().toURL().toString());
    }

    @GetMapping(value = "/token/refresh")
    public ResponseEntity<ApiResult<String, Errors>> refreshJwtToken(@RequestHeader String jwt) {
        Result<String, Errors> result = spotifyAuthService.refreshJwt(jwt);
        return switch (result) {
            case Result.Success(String refreshedJwt) -> ok(refreshedJwt);
            case Result.Failure(Errors errors) -> failure(errors);
        };
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<ApiResult<User, Errors>> getUserProfile(@RequestAttribute String userId){
        Result<SpotifyAuthData, Errors> getAuthDataResult = spotifyAuthService.getSpotifyAuthData(userId);
        if (getAuthDataResult.isFailure()){
            return failure(getAuthDataResult.getError());
        }
        Result<User, Errors> getProfileResult = spotifyAuthService.getUserProfile(getAuthDataResult.getValue());
        if (getProfileResult.isFailure()){
            return failure(getProfileResult.getError());
        }
        return ok(getProfileResult.getValue());
    }

    @GetMapping(value = "/login")
    public ResponseEntity<String> authorizationRedirect() {
        return ResponseEntity.ok(JSON.toJSONString(Map.of("url", Objects.requireNonNull(spotifyAuthService.redirect().getUrl()))));
    }

    @GetMapping(value = "/token/force/{username}")
    public String forceToken(@PathVariable String username) {
        return spotifyAuthService.forceJwt(username);
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Errors>> failure(Errors errors) {
        return new ResponseEntity<>(ApiResult.failure(errors), errors.httpStatus());
    }
}
