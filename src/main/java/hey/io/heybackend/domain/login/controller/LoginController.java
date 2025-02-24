package hey.io.heybackend.domain.login.controller;

import com.nimbusds.jose.JOSEException;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.login.service.LoginService;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.user.dto.TokenDto;
import hey.io.heybackend.domain.user.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "2. Login", description = "로그인 관련 API")
public class LoginController {

    private final LoginService loginService;
    private final RedisService redisService;


    /**
     * <p>로그인 키 발급</p>
     *
     * @return key
     */
    @GetMapping("/login/key")
    @Operation(summary = "로그인 키 발급", description = "UUID 기반의 로그인 키를 발급합니다.")
    public ApiResponse<String> generateLoginKey() {
        return ApiResponse.success(redisService.generateKey());
    }



    /**
     * <p>로그인</p>
     *
     * @param provider kakao, google, apple
     * @param code Authorization Code
     * @return 발급 토큰 정보
     */
    @GetMapping("/login/oauth2/code/{provider}")
    @Operation(summary = "로그인", description = "로그인을 수행합니다.")
    public ApiResponse<TokenDto> login(@PathVariable(name = "provider") Provider provider,
                                       @RequestParam(name = "code") String code,
                                       @RequestParam(name = "key") String key)
        throws ParseException, IOException, JOSEException {
        return ApiResponse.success(loginService.login(provider, code, key));
    }

    /**
     * <p>회원 가입</p>
     *
     * @param key redis Key
     * @return 발급 토큰 정보
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "Redis에 저장된 key를 사용하여 회원가입을 수행합니다.")
    public ApiResponse<TokenDto> register(@RequestParam(name = "key") String key) {
        return ApiResponse.success(loginService.register(key));
    }
}
