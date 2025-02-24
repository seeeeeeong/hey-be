package hey.io.heybackend.domain.user.service;

import hey.io.heybackend.domain.login.dto.SocialUserInfo;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Value("${jwt.access.expiration}")
    private Long refreshTokenDuration;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;


    public String generateKey() {
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(uuid, "PENDING", Duration.ofMinutes(15));
        return uuid;
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public void setSocialUserInfo(String key, SocialUserInfo socialUserInfo, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, socialUserInfo, duration);
    }

    public SocialUserInfo getSocialUserInfo(String key) {
        SocialUserInfo jsonValue = (SocialUserInfo) redisTemplate.opsForValue().get(key);

        if (ObjectUtils.isEmpty(jsonValue)) {
            return null;
        }

        return jsonValue;
    }

    public Long getMemberIdByRefreshToken(String refreshToken) {
        String key = "refreshToken:" + refreshToken;
        return (Long) redisTemplate.opsForValue().get(key);
    }

    public void setRefreshToken(Long memberId, String refreshToken) {
        String key = "refreshToken:" + refreshToken;
        redisTemplate.opsForValue().set(key, memberId, Duration.ofMillis(refreshTokenDuration));
    }

    public void addToBlackList(String token, Date expirationTime) {
        redisBlackListTemplate.opsForValue().set(token, "blacklisted", Duration.ofMillis(expirationTime.getTime()));
    }

    public boolean isBlacklisted(String token) {
        return redisBlackListTemplate.hasKey(token);
    }

    public void removeFromBlackList(String token) {
        redisBlackListTemplate.delete(token);
    }

}
