package hey.io.heybackend.domain.member.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider implements EnumMapperType {

    KAKAO("카카오"),
    GOOGLE("구글"),
    APPLE("애플");

    private final String description;


    @Override
    public String getCode() {
        return name();
    }
}


