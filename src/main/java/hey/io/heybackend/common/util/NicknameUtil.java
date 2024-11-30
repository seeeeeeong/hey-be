package hey.io.heybackend.common.util;

import hey.io.heybackend.domain.member.enums.NicknameType;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NicknameUtil {

    public String generateNickname() {
        String nickname;
        Random random = new Random();

        String nicknameBase = NicknameType.getRandomNickname();
        int randomNumber = random.nextInt(100000); // 0~99999
        nickname = String.format("%s_%05d", nicknameBase, randomNumber);

        return nickname;
    }

}
