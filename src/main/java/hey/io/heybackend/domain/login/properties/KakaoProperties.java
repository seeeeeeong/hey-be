package hey.io.heybackend.domain.login.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.kakao")
public record KakaoProperties(
    String clientId,
    String clientSecret,
    String tokenUrl,
    String userInfoUrl,
    String redirectUri
) {
}
