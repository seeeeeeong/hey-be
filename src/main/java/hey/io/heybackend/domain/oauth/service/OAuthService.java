package hey.io.heybackend.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.common.jwt.dto.JwtTokenDto;
import hey.io.heybackend.common.jwt.service.TokenManager;
import hey.io.heybackend.common.util.HeaderUtils;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.service.MemberService;
import hey.io.heybackend.domain.oauth.dto.OAuthAccessToken;
import hey.io.heybackend.domain.oauth.dto.OAuthUserProfile;
import hey.io.heybackend.domain.oauth.dto.OauthLoginResponse;
import hey.io.heybackend.domain.oauth.entity.Token;
import hey.io.heybackend.domain.oauth.properties.GoogleProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static hey.io.heybackend.common.exception.ErrorCode.PARSING_ERROR;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final TokenManager tokenManager;


    @Transactional
    public OauthLoginResponse googleLogin(HttpServletRequest request) {
        OAuthAccessToken accessToken = getAccessToken(request);
        OAuthUserProfile oAuthUserProfile = getUserProfile(accessToken);

        Member member = memberService.findMemberByEmail(oAuthUserProfile.getEmail());

//        if (member == null) {
//            member = memberService.registerMember(oAuthUserProfile.getEmail(), Provider.GOOGLE, accessToken.getRefreshToken());
//        }

        JwtTokenDto jwtTokenDto = tokenManager.createJwtTokenDto(member.getMemberId());

        return OauthLoginResponse.of(jwtTokenDto);
    }

    private OAuthAccessToken getAccessToken(HttpServletRequest request) {
        String authorizationCode = HeaderUtils.getAuthCode(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        MultiValueMap<String, String> httpBody = new LinkedMultiValueMap<>();
        httpBody.add("code", authorizationCode);
        httpBody.add("client_id", properties.getClientId());
        httpBody.add("client_secret", properties.getClientSecret());
        httpBody.add("redirect_uri", properties.getRedirectUri());
        httpBody.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestToken = new HttpEntity<>(httpBody,
                httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
                properties.getTokenUrl(), requestToken, String.class);

        try {
            return objectMapper.readValue(response.getBody(), OAuthAccessToken.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR, e);
        }

    }

    private OAuthUserProfile getUserProfile(OAuthAccessToken accessToken) {

        String userInfoUrl = properties.getUserInfoUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getAccessToken());
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
                httpEntity, String.class);

        try {
            return objectMapper.readValue(userInfoResponse.getBody(), OAuthUserProfile.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR, e);
        }
    }
}
