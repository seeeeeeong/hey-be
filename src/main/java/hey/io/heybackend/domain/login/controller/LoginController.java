package hey.io.heybackend.domain.login.controller;

import com.nimbusds.jose.JOSEException;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.login.service.LoginService;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.user.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "2. Login", description = "로그인 관련 API")
public class LoginController {

    private final LoginService loginService;

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
                                       @RequestParam(name = "code") String code)
        throws ParseException, IOException, JOSEException {
        return ApiResponse.success(loginService.login(provider, code));
    }
}
