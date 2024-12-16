package hey.io.heybackend.domain.login.client;

import static hey.io.heybackend.common.exception.ErrorCode.PARSING_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.util.OAuth2Util;
import hey.io.heybackend.domain.login.dto.SocialUserInfo;
import hey.io.heybackend.domain.login.properties.AppleProperties;
import hey.io.heybackend.domain.login.properties.GoogleProperties;
import hey.io.heybackend.domain.login.properties.KakaoProperties;
import hey.io.heybackend.domain.member.enums.Provider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OAuthClient {

    private final KakaoProperties kakaoProperties;
    private final GoogleProperties googleProperties;
    private final AppleProperties appleProperties;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OAuth2Util oAuth2Util;

    // 카카오 accessToken 요청
    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("client_id", kakaoProperties.clientId());
        body.put("client_secret", kakaoProperties.clientSecret());
        body.put("code", code);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", kakaoProperties.redirectUri());

        return oAuth2Util.getAccessToken(kakaoProperties.tokenUrl(), headers, body);
    }

    // 카카오 회원 정보 요청
    public SocialUserInfo getKakaoUserInfo(String accessToken)  {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                kakaoProperties.userInfoUrl(),
                HttpMethod.POST,
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();

        // id, email 추출
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            return SocialUserInfo.of(
                jsonNode.get("kakao_account").get("email").asText(),
                jsonNode.has("name") ? jsonNode.get("name").asText() : null,
                Provider.KAKAO,
                jsonNode.get("id").asText()
            );

        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR);
        }
    }

    // 구글 accessToken 요청
    public String getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("client_id", googleProperties.clientId());
        body.put("client_secret", googleProperties.clientSecret());
        body.put("code", code);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", googleProperties.redirectUri());

        return oAuth2Util.getAccessToken(googleProperties.tokenUrl(), headers, body);
    }

    // 구글 회원 정보 요청
    public SocialUserInfo getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(googleProperties.userInfoUrl(), HttpMethod.GET,
                httpEntity, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(userInfoResponse.getBody());

            String email = jsonNode.get("email").asText();
            String name = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
            String sub = jsonNode.get("sub").asText();

            return new SocialUserInfo(email, name, Provider.GOOGLE, sub);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR);
        }
    }

    // 애플 idToken 요청
    public String getAppleIdToken(String code) throws ParseException, IOException, JOSEException {
        String clientSecret = createClientSecret();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("client_id", appleProperties.clientId());
        body.put("client_secret", clientSecret);
        body.put("code", code);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", appleProperties.redirectUri());

        String idToken = oAuth2Util.getIdentityToken(appleProperties.tokenUrl(), headers, body);

        // 유효성 검사
        if (!validateAppleIdToken(idToken)) {
            throw new BusinessException(PARSING_ERROR);
        }

        return idToken;
    }

    // 애플 회원 정보 요청
    public SocialUserInfo getAppleUserInfo(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            return SocialUserInfo.of(
                claims.getStringClaim("email"),
                claims.getStringClaim("name"),
                Provider.APPLE,
                claims.getSubject()
            );
        } catch (ParseException e) {
            throw new BusinessException(PARSING_ERROR);
        }
    }

    // 유효성 검사
    private boolean validateAppleIdToken(String idToken) throws ParseException, JOSEException, IOException {
        SignedJWT signedJWT = SignedJWT.parse(idToken);

        // 공개 키 조회
        JWKSet jwkSet = JWKSet.load(new URL(appleProperties.publicKeyUrl()));
        JWK jwk = jwkSet.getKeyByKeyId(signedJWT.getHeader().getKeyID());

        // 검증을 위한 RSA 공개 키 생성
        RSAKey rsaKey = (RSAKey) jwk;
        JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

        // JWT 서명 검증
        return signedJWT.verify(verifier);
    }

    // 클라이언트 시크릿 생성
    private String createClientSecret() {
        PrivateKey privateKey = getPrivateKey();

        long nowMillis  = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        return Jwts.builder()
                .setHeaderParam("kid", appleProperties.keyId())
                .setIssuer(appleProperties.teamId())
                .setIssuedAt(now)
                .setExpiration(new Date(nowMillis + 86400000))// 1일
                .setAudience("https://appleid.apple.com")
                .setSubject(appleProperties.clientId())
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }

    // 개인키 조회
    private PrivateKey getPrivateKey() {
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.privateKey());

            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
            return converter.getPrivateKey(privateKeyInfo);
        } catch (PEMException e) {
            throw new RuntimeException(e);
        }
    }
}
