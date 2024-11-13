package hey.io.heybackend.domain.oauth2.apple;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.Getter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {
    private final OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;
    private final String path;
    private final String keyId;
    private final String teamId;
    private final String clientId;
    private final String url;

    public CustomRequestEntityConverter(AppleProperties properties) {
        this.defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        this.path = properties.getPath();
        this.keyId = properties.getKid();
        this.teamId = properties.getTid();
        this.clientId = properties.getCid();
        this.url = properties.getUrl();
    }

    /**
     * <p>OAuth2AuthorizationCodeGrantRequest를 RequestEntity로 변환</p>
     *
     * Apple 로그인 요청인 경우, 'client_secret'을 생성하여 추가
     *
     * @param req OAuth2AuthorizationCodeGrantRequest
     * @return 변환된 RequestEntity 객체
     */
    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
        RequestEntity<?> entity = defaultConverter.convert(req);

        String registrationId = req.getClientRegistration().getRegistrationId();
        MultiValueMap<String, String> params = (MultiValueMap<String, String>) entity.getBody();

        // ID가 "apple"를 포함할 경우, Apple의 client_secret 추가
        if (registrationId.contains("apple")) {
            try {
                params.set("client_secret", createClientSecret());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new RequestEntity<>(params, entity.getHeaders(),
                entity.getMethod(), entity.getUrl());
    }

    /**
     * <p>ClassPath에서 Apple Private Key 조회</p>
     *
     * Private Key를 통해 client_secret 생성
     * @return PrivateKey
     *
     */
    public PrivateKey getPrivateKey() throws IOException {
        // ClassPath에서 PrivateKey 파일 로드
        ClassPathResource resource = new ClassPathResource(path);

        // PrivateKey 파일을 입력 스트림으로 변환
        InputStream in = resource.getInputStream();

        // PEMParser를 사용하여 PrivateKey 파일 파싱
        PEMParser pemParser = new PEMParser(new StringReader(IOUtils.toString(in, StandardCharsets.UTF_8)));
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

        // JcaPEMKeyConverter를 사용하여 PrivateKey 객체 변환
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        return converter.getPrivateKey(object);
    }

    /**
     * <p>Apple의 client_secret을 JWT 형식으로 생성</p>
     *
     * Apple OAuth 인증을 위한 client_secret은 JWT (JSON Web Token) 형식으로 생성되며,
     * 이를 사용하여 Apple에 인증 요청
     *
     * @return 생성된 client_secret JWT 토큰
     */
    public String createClientSecret() throws IOException {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)))// 만료 시간
                .setAudience(url)
                .setSubject(clientId)
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }
}
