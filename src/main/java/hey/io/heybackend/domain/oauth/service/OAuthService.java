package hey.io.heybackend.domain.oauth.service;

import com.nimbusds.jose.JOSEException;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.oauth.client.OAuthClient;
import hey.io.heybackend.domain.oauth.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthClient oAuthClient;
    private final OAuthLoginService OAuthLoginService;

    /**
     * <p>소셜 로그인</p>
     *
     * @param provider ("kakao", "google", "apple")
     * @param code 인증 코드
     * @return 토큰 정보
     */
    @Transactional
    public TokenDto login(String provider, String code) throws ParseException, IOException, JOSEException {
        return switch (provider.toLowerCase()) {
            case "kakao" -> handleLogin(oAuthClient.getKakaoAccessToken(code), Provider.KAKAO, oAuthClient::getKakaoUserInfo);
            case "google" -> handleLogin(oAuthClient.getGoogleAccessToken(code), Provider.GOOGLE, oAuthClient::getGoogleUserInfo);
            case "apple" -> handleAppleLogin(oAuthClient.getAppleIdToken(code), Provider.APPLE);
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_LOGIN_TYPE);
        };
    }

    /**
     * <p>카카오/구글 로그인</p>
     *
     * @param accessToken accessToken 토큰
     * @param provider ("kakao", "google")
     * @param userInfoFetcher 사용자 정보 인터페이스
     * @return 토큰 정보
     */
    private TokenDto handleLogin(String accessToken, Provider provider, UserInfoFetcher userInfoFetcher) {
        try {
            // 소셜 회원 정보 조회
            Map<String, Object> userInfo = userInfoFetcher.fetchUserInfo(accessToken);
            SocialUserInfo socialUserInfo = provider.mapUserInfo(userInfo);
            return processLogin(socialUserInfo);
        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.PARSING_ERROR);
        }
    }

    // 소셜 회원 정보 조회 인터페이스
    @FunctionalInterface
    public interface UserInfoFetcher {
        Map<String, Object> fetchUserInfo(String accessToken);
    }

    /**
     * <p>애플 로그인</p>
     *
     * @param idToken ID 토큰
     * @param provider ("apple")
     * @return 토큰 정보
     */
    private TokenDto handleAppleLogin(String idToken, Provider provider) {
        try {
            // 소셜 회원 정보 조회
            Map<String, Object> userInfo = Map.of("id_token", idToken);
            SocialUserInfo socialUserInfo = provider.mapUserInfo(userInfo);
            return processLogin(socialUserInfo);
        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.PARSING_ERROR);
        }
    }

    /**
     * <p>회원 가입 및 로그인</p>
     *
     * @param socialUserInfo 소셜 회원 정보
     * @return 토큰 정보
     */
    private TokenDto processLogin(SocialUserInfo socialUserInfo) {
        // 회원 가입 및 로그인 처리, 토큰 발급
        return OAuthLoginService.processLogin(socialUserInfo);
    }
}
