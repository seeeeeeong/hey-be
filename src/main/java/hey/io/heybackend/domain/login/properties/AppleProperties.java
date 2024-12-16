package hey.io.heybackend.domain.login.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.apple")
public record AppleProperties(
    String clientId,
    String keyId,
    String teamId,
    String privateKey,
    String publicKeyUrl,
    String tokenUrl,
    String redirectUri
) {
}
