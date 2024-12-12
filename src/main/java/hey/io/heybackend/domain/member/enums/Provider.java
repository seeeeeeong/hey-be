package hey.io.heybackend.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {

    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE");

    private final String description;

}


