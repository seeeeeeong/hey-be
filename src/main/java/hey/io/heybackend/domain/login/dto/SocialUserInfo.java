package hey.io.heybackend.domain.login.dto;


import hey.io.heybackend.domain.member.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SocialUserInfo {
    private final String email;
    private final String name;
    private final Provider provider;
    private final String providerUid;

    public static SocialUserInfo of(String email, String name, Provider provider, String providerUid) {
        return SocialUserInfo.builder()
            .email(email)
            .name(name)
            .provider(provider)
            .providerUid(providerUid)
            .build();
    }
}
