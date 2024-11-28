package hey.io.heybackend.domain.oauth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.JwtTokenProvider;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.jwt.service.TokenService;
import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.service.MemberService;
import hey.io.heybackend.domain.oauth.client.OAuthClient;
import hey.io.heybackend.domain.oauth.properties.AppleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthClient oAuthClient;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthService authService;
    private final MemberService memberService;
    private final TokenService tokenService;

    private final AppleProperties appleProperties;

    @Transactional
    public TokenDto login(String provider, String code) throws ParseException, IOException, JOSEException {
        return switch (provider.toLowerCase()) {
            case "kakao" -> loginKakao(code);
            case "google" -> loginGoogle(code);
            case "apple" -> loginApple(code);
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_LOGIN_TYPE);
        };
    }

    private TokenDto loginKakao(String code) {
        String kakaoAccessToken = oAuthClient.getKakaoAccessToken(code);
        Map<String, Object> userInfo = oAuthClient.getKakaoUserInfo(kakaoAccessToken);

        String email = (String) userInfo.get("email");
        String providerUid = (String) userInfo.get("id");
        String name = (String) userInfo.get("name");
        Provider provider = Provider.KAKAO;

        Member member = memberService.insertOrUpdateMember(email, name, provider, providerUid);
        memberService.insertOrUpdateSocialAccount(member, provider, providerUid);
        authService.insertUserAuth(member);

        TokenDto tokenDto = insertToken(member);
        return tokenDto;
    }

    private TokenDto loginGoogle(String code)  {
        String googleAccessToken = oAuthClient.getGoogleAccessToken(code);
        Map<String, Object> userInfo = oAuthClient.getGoogleUserInfo(googleAccessToken);

        String email = (String) userInfo.get("email");
        String providerUid = (String) userInfo.get("sub");
        String name = (String) userInfo.get("name");
        Provider provider = Provider.GOOGLE;

        Member member = memberService.insertOrUpdateMember(email, name, provider, providerUid);
        memberService.insertOrUpdateSocialAccount(member, provider, providerUid);
        authService.insertUserAuth(member);

        TokenDto tokenDto = insertToken(member);
        return tokenDto;
    }

    private TokenDto loginApple(String code) throws ParseException, IOException, JOSEException {
        String appleIdToken = oAuthClient.getAppleIdToken(code);

        if (!validateAppleIdToken(appleIdToken)) {
            throw new RuntimeException();
        }

        SignedJWT signedJWT = SignedJWT.parse(appleIdToken);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        String email = claims.getStringClaim("email");
        String name = claims.getStringClaim("name");
        String providerUid = claims.getSubject();
        Provider provider = Provider.APPLE;

        Member member = memberService.insertOrUpdateMember(email, name, provider, providerUid);
        memberService.insertOrUpdateSocialAccount(member, provider, providerUid);
        authService.insertUserAuth(member);

        TokenDto tokenDto = insertToken(member);
        return tokenDto;
    }

    private boolean validateAppleIdToken(String idToken) throws ParseException, JOSEException, IOException {
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        // 1. Apple의 공개 키 조회
        JWKSet jwkSet = JWKSet.load(new URL(appleProperties.getPublicKeyUrl()));
        JWK jwk = jwkSet.getKeyByKeyId(signedJWT.getHeader().getKeyID());
        // 2. 검증을 위한 RSA 공개 키 생성E
        RSAKey rsaKey = (RSAKey) jwk;
        JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
        // 3. JWT 서명 검증
        return signedJWT.verify(verifier);
    }

    private TokenDto insertToken(Member member) {
        TokenDto tokenDto = jwtTokenProvider.createToken(member);
        tokenService.saveRefreshToken(tokenDto.getMemberId(), tokenDto.getRefreshToken());
        return tokenDto;
    }

}
