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
    public TokenDto login(String provider, String code) throws ParseException, IOException, JOSEException {
        return switch (provider.toLowerCase()) {
            case "kakao" -> kakaoLogin(oAuthClient.getKakaoAccessToken(code));
            case "google" -> googleLogin(oAuthClient.getGoogleAccessToken(code));
            case "apple" -> appleLogin(oAuthClient.getAppleIdToken(code));
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_LOGIN_TYPE);
        };
    }

    // 카카오 로그인
    private TokenDto kakaoLogin(String accessToken) {
        // accessToken으로 사용자 정보 조회
        Map<String, Object> userInfo = oAuthClient.getKakaoUserInfo(accessToken);

        // 사용자 정보 SocialUserInfo 변환
        SocialUserInfo socialUserInfo = SocialUserInfo.of(
            (String) userInfo.get("email"),
            (String) userInfo.get("name"),
            Provider.KAKAO,
            (String) userInfo.get("id")
        );

        // 회원 및 소설 계정 저장/업데이트
        Member member = socialAccountService.mergeMemberAndSocialAccount(socialUserInfo);

        // 토큰 저장
        return tokenService.insertToken(member);
    }

    // 구글 로그인
    private TokenDto googleLogin(String accessToken) {
        // accessToken으로 사용자 정보 조회
        Map<String, Object> userInfo = oAuthClient.getGoogleUserInfo(accessToken);

        // 사용자 정보 SocialUserInfo 변환
        SocialUserInfo socialUserInfo = SocialUserInfo.of(
            (String) userInfo.get("email"),
            (String) userInfo.get("name"),
            Provider.GOOGLE,
            (String) userInfo.get("sub")
        );

        // 회원 및 소설 계정 저장/업데이트
        Member member = socialAccountService.mergeMemberAndSocialAccount(socialUserInfo);

        // 토큰 저장
        return tokenService.insertToken(member);
    }

    // 애플 로그인
    private TokenDto appleLogin(String idToken) throws ParseException {
        // idToken으로 사용자 정보 조회
        Map<String, Object> userInfo = Map.of("id_token", idToken);
        SignedJWT signedJWT = SignedJWT.parse((String) userInfo.get("id_token"));
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        // 사용자 정보 SocialUserInfo 변환
        SocialUserInfo socialUserInfo =  SocialUserInfo.of(
            claims.getStringClaim("email"),
            claims.getStringClaim("name"),
            Provider.APPLE,
            claims.getSubject()
        );

        // 회원 및 소설 계정 저장/업데이트
        Member member = socialAccountService.mergeMemberAndSocialAccount(socialUserInfo);

        // 토큰 저장
        return tokenService.insertToken(member);
    }
}

