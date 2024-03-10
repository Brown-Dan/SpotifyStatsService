package uk.co.spotistats.spotistatsservice.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.spotistats.spotistatsservice.Controller.Model.ApiResult;
import uk.co.spotistats.spotistatsservice.Domain.Response.Error;
import uk.co.spotistats.spotistatsservice.Domain.UserAuthData;
import uk.co.spotistats.spotistatsservice.Service.UserService;

@Controller
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResult<UserAuthData, Error>> register(@RequestBody UserAuthData userAuthData) {
        return userService.register(userAuthData).<ResponseEntity<ApiResult<UserAuthData, Error>>>
                map(this::badRequest).orElseGet(() -> ok(userAuthData));
    }

    private <T> ResponseEntity<ApiResult<T, Error>> ok(T body) {
        return ResponseEntity.ok(ApiResult.success(body));
    }

    private <T> ResponseEntity<ApiResult<T, Error>> badRequest(Error error) {
        return new ResponseEntity<>(ApiResult.failure(error), HttpStatus.BAD_REQUEST);
    }
}
