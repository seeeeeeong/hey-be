package hey.io.heybackend.common.util;

import hey.io.heybackend.domain.member.enums.NicknameType;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class NicknameGenerator {

    public String generateNickname() {
        String nickname;
        Random random = new Random();

        String nicknameBase = NicknameType.getRandomNickname();
        int randomNumber = random.nextInt(100000); // 0~99999
        nickname = String.format("%s_%05d", nicknameBase, randomNumber);

        return nickname;
    }

}
