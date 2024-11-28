package hey.io.heybackend.domain.oauth.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.util.OAuth2Util;
import hey.io.heybackend.domain.oauth.properties.AppleProperties;
import hey.io.heybackend.domain.oauth.properties.GoogleProperties;
import hey.io.heybackend.domain.oauth.properties.KakaoProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static hey.io.heybackend.common.exception.ErrorCode.PARSING_ERROR;

@Component
@RequiredArgsConstructor
public class OAuthClient {

    private final KakaoProperties kakaoProperties;
    private final GoogleProperties googleProperties;
    private final AppleProperties appleProperties;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OAuth2Util oAuth2Util;

    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, String> body = new HashMap<>();
        body.put("client_id", kakaoProperties.getClientId());
        body.put("client_secret", kakaoProperties.getClientSecret());
        body.put("code", code);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", kakaoProperties.getRedirectUri());
        return oAuth2Util.getAccessToken(kakaoProperties.getTokenUrl(), headers, body);
    }

    public Map getKakaoUserInfo(String accessToken)  {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                kakaoProperties.getUserInfoUrl(),
                HttpMethod.POST,
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", jsonNode.get("id").asText());
            userInfo.put("email", jsonNode.get("kakao_account").get("email").asText());

            return userInfo;
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR);
        }
    }

    public String getGoogleAccessToken(String code) {
//        String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("client_id", googleProperties.getClientId());
        body.put("client_secret", googleProperties.getClientSecret());
        body.put("code", code);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", googleProperties.getRedirectUri());
        return oAuth2Util.getAccessToken(googleProperties.getTokenUrl(), headers, body);
    }

    public Map getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(googleProperties.getUserInfoUrl(), HttpMethod.GET,
                httpEntity, String.class);

        try {
            return objectMapper.readValue(userInfoResponse.getBody(), Map.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR);
        }
    }

    public String getAppleIdToken(String code) throws ParseException, IOException, JOSEException {
        String clientSecret = createClientSecret();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> body = new HashMap<>();
        body.put("client_id", appleProperties.getClientId());
        body.put("client_secret", clientSecret);
        body.put("code", code);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", appleProperties.getRedirectUri());

        String idToken = oAuth2Util.getIdentityToken(appleProperties.getTokenUrl(), headers, body);

        if (!validateAppleIdToken(idToken)) {
            throw new BusinessException(PARSING_ERROR);
        }

        return idToken;
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

    private String createClientSecret() {
        PrivateKey privateKey = getPrivateKey();
        long nowMillis  = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        return Jwts.builder()
                .setHeaderParam("kid", appleProperties.getKeyId())
                .setIssuer(appleProperties.getTeamId())
                .setIssuedAt(now)
                .setExpiration(new Date(nowMillis + 86400000))// 1일
                .setAudience("https://appleid.apple.com")
                .setSubject(appleProperties.getClientId())
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());

            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
            return converter.getPrivateKey(privateKeyInfo);
        } catch (PEMException e) {
            throw new RuntimeException(e);
        }
    }

}
