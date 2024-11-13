package hey.io.heybackend.domain.oauth2.userinfo;

import java.util.Map;

public interface OAuth2UserInfo {

    Map<String, Object> getAttributes();

    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();

}
