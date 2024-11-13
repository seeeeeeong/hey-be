package hey.io.heybackend.domain.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.oauth2.userinfo.AppleUserInfo;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    /**
     * <p>OAuth2 로그인 요청 후 사용자 정보 조회</p>
     *
     * @return PrincipalDetails
     */
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        // 1. 사용자 정보 조회
        OAuth2UserInfo oAuth2UserInfo = null;
        OAuth2User oAuth2User;

        // 2. Apple, Google, Kakao 분기 처리
        if (userRequest.getClientRegistration().getRegistrationId().equals("apple")) {
            // Apple 로그인의 경우, id_token 디코딩
            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
            oAuth2UserInfo = new AppleUserInfo(decodeJwtTokenPayload(idToken));
        } else {
            oAuth2User = super.loadUser(userRequest);

            if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
                log.info("Google Login Request");
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
                log.info("Kakao Login Request");
                oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            }
        }

        // 3. Provider에 따른 사용자 정보 추출
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        Provider provider = Provider.valueOf(oAuth2UserInfo.getProvider().toUpperCase());
        String providerUid = oAuth2UserInfo.getProviderId();

        // 4. PrincipalDetails 객체 생성
        PrincipalDetails principalDetails = new PrincipalDetails(email, name, provider, providerUid, oAuth2UserInfo.getAttributes());

        log.info("principalDetails: " + principalDetails);

        return principalDetails;
    }

    /**
     * <p>JWT 토큰의 페이로드 디코딩</p>
     *
     * @param jwtToken Apple의 id_token
     * @return decodedPayload
     */
    public Map<String, Object> decodeJwtTokenPayload(String jwtToken) {
        Map<String, Object> jwtClaims = new HashMap<>();
        try {
            // JWT 토큰 분리
            String[] parts = jwtToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            // 페이로드 디코딩
            byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            // 디코딩된 문자열 JSON 객체로 변환
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(decodedString, Map.class);

            // 추출된 정보 반환
            jwtClaims.putAll(map);

        } catch (JsonProcessingException e) {
        log.error("decodeJwtToken: {}-{} / jwtToken : {}", e.getMessage(), e.getCause(), jwtToken);
        }
        return jwtClaims;
    }
}
