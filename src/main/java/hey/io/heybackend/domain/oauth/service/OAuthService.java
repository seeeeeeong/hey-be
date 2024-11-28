package hey.io.heybackend.domain.oauth.service;

import com.nimbusds.jose.JOSEException;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.service.MemberOAuthService;
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
    private final MemberOAuthService memberOAuthService;

    @Transactional
    public TokenDto login(String provider, String code) throws ParseException, IOException, JOSEException {
        return switch (provider.toLowerCase()) {
            case "kakao" -> handleLogin(oAuthClient.getKakaoAccessToken(code), Provider.KAKAO, oAuthClient::getKakaoUserInfo);
            case "google" -> handleLogin(oAuthClient.getGoogleAccessToken(code), Provider.GOOGLE, oAuthClient::getGoogleUserInfo);
            case "apple" -> handleAppleLogin(oAuthClient.getAppleIdToken(code), Provider.APPLE);
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_LOGIN_TYPE);
        };
    }

    private TokenDto handleLogin(String accessToken, Provider provider, UserInfoFetcher userInfoFetcher) {
        Map<String, Object> userInfo = userInfoFetcher.fetchUserInfo(accessToken);

        try {
            SocialUserInfo socialUserInfo = provider.mapUserInfo(userInfo);
            return processLogin(socialUserInfo);
        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.PARSING_ERROR);
        }
    }

    @FunctionalInterface
    public interface UserInfoFetcher {
        Map<String, Object> fetchUserInfo(String accessToken);
    }

    private TokenDto handleAppleLogin(String idToken, Provider provider) {
        try {
            Map<String, Object> userInfo = Map.of("id_token", idToken);
            SocialUserInfo socialUserInfo = provider.mapUserInfo(userInfo);
            return processLogin(socialUserInfo);
        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.PARSING_ERROR);
        }
    }

    private TokenDto processLogin(SocialUserInfo socialUserInfo) {
        return memberOAuthService.processLogin(socialUserInfo);
    }
}
