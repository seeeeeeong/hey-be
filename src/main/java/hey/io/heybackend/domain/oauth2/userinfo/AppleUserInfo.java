package hey.io.heybackend.domain.oauth2.userinfo;

import java.util.Map;

public class AppleUserInfo implements OAuth2UserInfo{

    private Map<String,Object> attributes;

    public AppleUserInfo(Map<String,Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return null;
    }

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return null;
    }
}
