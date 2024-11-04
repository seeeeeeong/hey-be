package hey.io.heybackend.domain.member.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PushType implements EnumMapperType {

    PERFORMANCE("PERFORMANCE"),
    TICKET("TICKET");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }

}
