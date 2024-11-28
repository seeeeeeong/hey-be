package hey.io.heybackend.domain.member.enums;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hey.io.heybackend.domain.oauth.dto.SocialUserInfo;
import hey.io.heybackend.common.mapper.EnumMapperType;
import hey.io.heybackend.domain.oauth.mapper.AppleUserInfoMapper;
import hey.io.heybackend.domain.oauth.mapper.GoogleUserInfoMapper;
import hey.io.heybackend.domain.oauth.mapper.KakaoUserInfoMapper;
import hey.io.heybackend.domain.oauth.mapper.UserInfoMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.ParseException;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Provider implements EnumMapperType {
    KAKAO("KAKAO", new KakaoUserInfoMapper()),
    GOOGLE("GOOGLE", new GoogleUserInfoMapper()),
    APPLE("APPLE", new AppleUserInfoMapper());

    private final String description; // Enum 설명
    private final UserInfoMapper userInfoMapper;

    @Override
    public String getCode() {
        return name();
    }

    public SocialUserInfo mapUserInfo(Map<String, Object> userInfo) throws ParseException {
        return userInfoMapper.mapUserInfo(userInfo);
    }
}

