package hey.io.heybackend.domain.login.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.domain.login.client.OAuthClient;
import hey.io.heybackend.domain.login.dto.SocialUserInfo;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.service.SocialAccountService;
import hey.io.heybackend.domain.user.dto.TokenDto;
import hey.io.heybackend.domain.user.service.TokenService;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final OAuthClient oAuthClient;

    private final SocialAccountService socialAccountService;
    private final TokenService tokenService;

    /**
     * <p>소셜 로그인</p>
     *
     * @param provider ("kakao", "google", "apple")
     * @param code 인증 코드
     * @return 토큰 정보
     */
    @Transactional
    public TokenDto login(Provider provider, String code) throws ParseException, IOException, JOSEException {
        return switch (provider) {
            case KAKAO -> kakaoLogin(code);
            case GOOGLE -> googleLogin(code);
            case APPLE -> appleLogin(code);
        };
    }

    // 카카오 로그인
    private TokenDto kakaoLogin(String code) {

        String accessToken = oAuthClient.getKakaoAccessToken(code);
        SocialUserInfo socialUserInfo = oAuthClient.getKakaoUserInfo(accessToken);

        // 회원 및 소설 계정 저장/업데이트
        Member member = socialAccountService.mergeMemberAndSocialAccount(socialUserInfo);

        // 토큰 저장
        return tokenService.insertToken(member);
    }

    // 구글 로그인
    private TokenDto googleLogin(String code) {
        String accessToken = oAuthClient.getGoogleAccessToken(code);
        SocialUserInfo socialUserInfo = oAuthClient.getGoogleUserInfo(accessToken);

        // 회원 및 소설 계정 저장/업데이트
        Member member = socialAccountService.mergeMemberAndSocialAccount(socialUserInfo);

        // 토큰 저장
        return tokenService.insertToken(member);
    }

    // 애플 로그인
    private TokenDto appleLogin(String code) throws ParseException, IOException, JOSEException {
        String idToken = oAuthClient.getAppleIdToken(code);
        SocialUserInfo socialUserInfo = oAuthClient.getAppleUserInfo(idToken);

        // 회원 및 소설 계정 저장/업데이트
        Member member = socialAccountService.mergeMemberAndSocialAccount(socialUserInfo);

        // 토큰 저장
        return tokenService.insertToken(member);
    }
}

