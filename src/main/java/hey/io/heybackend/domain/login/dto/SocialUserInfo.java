package hey.io.heybackend.domain.login.dto;

import hey.io.heybackend.domain.member.enums.Provider;

public record SocialUserInfo(
    String email,
    String name,
    Provider provider,
    String providerUid
) {
    public static SocialUserInfo of(String email, String name, Provider provider, String providerUid) {
        return new SocialUserInfo(email, name, provider, providerUid);
    }
}
