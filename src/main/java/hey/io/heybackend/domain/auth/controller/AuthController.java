package hey.io.heybackend.domain.auth.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.auth.dto.LoginResponse;
import hey.io.heybackend.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/oauth2/callback/google")
    public ApiResponse<LoginResponse> googleLogin(@RequestParam(name = "code") String code) throws JsonProcessingException {
        return ApiResponse.success(authService.googleLogin(code));
    }

    @GetMapping("/login/oauth2/callback/kakao")
    public ApiResponse<LoginResponse> kakaoLogin(@RequestParam(name = "code") String code) throws JsonProcessingException {
        return ApiResponse.success(authService.kakaoLogin(code));
    }

}
