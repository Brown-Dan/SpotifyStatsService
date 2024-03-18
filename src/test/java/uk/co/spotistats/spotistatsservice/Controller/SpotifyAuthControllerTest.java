package uk.co.spotistats.spotistatsservice.Controller;


import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Controller.Model.Errors;
import uk.co.spotistats.spotistatsservice.Domain.Model.Error;
import uk.co.spotistats.spotistatsservice.Domain.Response.Api.Result;
import uk.co.spotistats.spotistatsservice.Service.SpotifyAuthService;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpotifyAuthControllerTest {

    @Mock
    private SpotifyAuthService spotifyAuthService;

    private SpotifyAuthController spotifyAuthController;

    @BeforeEach
    void setUp() {
        spotifyAuthController = new SpotifyAuthController(spotifyAuthService);
    }

    @Test
    void authenticationCallback_givenValidAccessTokenShouldReturnNewJwtToken(){
        String accessToken = "validAccessToken";

        when(spotifyAuthService.exchangeAccessToken(any())).thenReturn(new Result.Success<>("validJwt"));

        ResponseEntity<ApiResult<String, Errors>> result = spotifyAuthController.authenticationCallback(accessToken);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>("validJwt"));
        verify(spotifyAuthService).exchangeAccessToken(accessToken);
    }

    @Test
    void authenticationCallback_givenInvalidAccessTokenShouldReturnError(){
        String accessToken = "invalidAccessToken";
        Errors errors = Errors.fromError(Error.unknownError("spotify", "Exception occurred within spotify client"));

        when(spotifyAuthService.exchangeAccessToken(any())).thenReturn(new Result.Failure<>(errors));

        ResponseEntity<ApiResult<String, Errors>> result = spotifyAuthController.authenticationCallback(accessToken);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(errors));
        verify(spotifyAuthService).exchangeAccessToken(accessToken);
    }

    @Test
    void refreshJwtToken_givenNonExpiredJwtToken_shouldReturnRefreshedToken(){
        String jwt = "nonExpiredToken";
        String refreshedJwt = "refreshedJwtToken";

        when(spotifyAuthService.refreshJwt(any())).thenReturn(new Result.Success<>(refreshedJwt));

        ResponseEntity<ApiResult<String, Errors>> result = spotifyAuthController.refreshJwtToken(jwt);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Success<>(refreshedJwt));
        verify(spotifyAuthService).refreshJwt(jwt);
    }

    @Test
    void refreshJwtToken_givenExpiredJwtToken_shouldReturnError(){
        JWTVerificationException jwtVerificationException = new JWTVerificationException("error");
        String jwt = "expiredToken";
        Errors expectedErrors = Errors.fromError(Error.jwtVerificationException(jwtVerificationException));

        when(spotifyAuthService.refreshJwt(any())).thenReturn(new Result.Failure<>(expectedErrors));

        ResponseEntity<ApiResult<String, Errors>> result = spotifyAuthController.refreshJwtToken(jwt);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo(new ApiResult.Failure<>(expectedErrors));
        verify(spotifyAuthService).refreshJwt(jwt);
    }

    @Test
    void authorizationRedirect_shouldRedirectToSpotify() throws URISyntaxException, MalformedURLException {
        RedirectView redirectView = new RedirectView(new URIBuilder()
                .setScheme("https")
                .setHost("accounts.spotify.com")
                .setPath("/authorize")
                .setParameter("client_id", System.getenv("SPOTIFY_CLIENT_ID"))
                .setParameter("response_type", "code")
                .setParameter("redirect_uri", "https://spotifystats.co.uk/authenticate/callback")
                .setParameter("scope", "playlist-read-private user-follow-read user-top-read user-read-recently-played user-library-read user-read-private user-read-email playlist-modify-public playlist-modify-private")
                .build().toURL().toString());

        when(spotifyAuthService.redirect()).thenReturn(redirectView);

        RedirectView result = spotifyAuthController.authorizationRedirect();

        assertThat(result).isEqualTo(redirectView);
        verify(spotifyAuthService).redirect();
    }
}
