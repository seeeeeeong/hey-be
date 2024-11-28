package hey.io.heybackend.domain.oauth.mapper;

import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.oauth.dto.SocialUserInfo;

import java.util.Map;

public class KakaoUserInfoMapper implements UserInfoMapper {
    @Override
    public SocialUserInfo mapUserInfo(Map<String, Object> userInfo) {
        return new SocialUserInfo(
                (String) userInfo.get("email"),
                (String) userInfo.get("name"),
                Provider.KAKAO,
                (String) userInfo.get("id")
                );
    }
}

