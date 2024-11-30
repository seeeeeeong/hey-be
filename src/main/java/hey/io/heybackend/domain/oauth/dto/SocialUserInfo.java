package hey.io.heybackend.domain.oauth.dto;


import hey.io.heybackend.domain.member.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialUserInfo {
    private final String email;
    private final String name;
    private final Provider provider;
    private final String providerUid;
}
