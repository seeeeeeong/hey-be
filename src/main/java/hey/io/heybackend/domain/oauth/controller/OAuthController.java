package hey.io.heybackend.domain.oauth.controller;

import com.nimbusds.jose.JOSEException;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * <p>소셜 로그인</p>
     *
     * @param provider ("kakao", "google", "apple")
     * @param code 인증 코드
     * @return 토큰 정보
     */
    @GetMapping("/login/oauth2/code/{provider}")
    public ApiResponse<TokenDto> login(@PathVariable(name = "provider") String provider,
                                       @RequestParam(name = "code") String code) throws ParseException, IOException, JOSEException {
        return ApiResponse.success(oAuthService.login(provider, code));
    }
}
