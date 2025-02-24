package hey.io.heybackend.domain.user.service;

import hey.io.heybackend.domain.login.dto.SocialUserInfo;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

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

}
