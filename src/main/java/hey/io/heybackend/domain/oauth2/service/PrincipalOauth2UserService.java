package hey.io.heybackend.domain.oauth2.service;

import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.oauth2.userinfo.GoogleUserInfo;
import hey.io.heybackend.domain.oauth2.userinfo.KakaoUserInfo;
import hey.io.heybackend.domain.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

//    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        // 1. 사용자 정보 조회
        OAuth2UserInfo oAuth2UserInfo = null;
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. Provider에 따라 유저 정보를 통해 oAuth2UserInfo 객체 생성
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            log.info("Google Login Request");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            log.info("Kakao Login Request");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }

        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        Provider provider = Provider.valueOf(oAuth2UserInfo.getProvider().toUpperCase());
        String providerUid = oAuth2UserInfo.getProviderId();

        log.info("email: " + email);
        log.info("name: " + name);
        log.info("provider: " + provider);
        log.info("providerUid: " + providerUid);

        // 3. oAuth2UserInfo를 통해 PrincipalDetails 객체를 생성해서 반환
        PrincipalDetails principalDetails = new PrincipalDetails(email, name, provider, providerUid, oAuth2UserInfo.getAttributes());

        log.info("principalDetails: " + principalDetails);


        return principalDetails;
    }
}
