package hey.io.heybackend.domain.oauth.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.oauth.dto.LoginResponse;
import hey.io.heybackend.domain.oauth.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "3. 소셜 로그인 및 회원가입", description = "소셜 로그인 및 회원가입 관련 API")
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * <p>구글 로그인</p>
     *
     * @param code 구글 authCode
     * @return accessToken, refreshToken
     */
    @Operation(summary = "구글 로그인", description = "구글 계정으로 로그인합니다.")
    @GetMapping("/login/oauth2/callback/google")
    public ApiResponse<LoginResponse> googleLogin(@RequestParam(name = "code") String code) throws JsonProcessingException {
        return ApiResponse.success(oAuthService.googleLogin(code));
    }

    /**
     * <p>카카오 로그인</p>
     *
     * @param code 카카오 authCode
     * @return accessToken, refreshToken
     */
    @Operation(summary = "카카오 로그인", description = "카카오 계정으로 로그인합니다.")
    @GetMapping("/login/oauth2/callback/kakao")
    public ApiResponse<LoginResponse> kakaoLogin(@RequestParam(name = "code") String code) throws JsonProcessingException {
        return ApiResponse.success(oAuthService.kakaoLogin(code));
    }

    /**
     * <p>애플 로그인</p>
     **
     * @param code 애플 authCode
     * @return accessToken, refreshToken
     */
    @GetMapping
    public ApiResponse<LoginResponse> appleLogin(@RequestParam(name = "code") String code) throws ParseException, JOSEException, IOException, JOSEException {
        return ApiResponse.success(oAuthService.appleLogin(code));
    }

}
