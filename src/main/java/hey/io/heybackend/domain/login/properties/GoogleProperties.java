package hey.io.heybackend.domain.login.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.google")
public record GoogleProperties(
    String clientId,
    String clientSecret,
    String tokenUrl,
    String userInfoUrl,
    String redirectUri
) {
}
