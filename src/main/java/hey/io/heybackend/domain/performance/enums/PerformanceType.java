package hey.io.heybackend.domain.performance.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceType implements EnumMapperType {

    CONCERT_IN("국내 콘서트"),
    CONCERT_EX("내한 콘서트"),
    FESTIVAL_IN("페스티벌"),
    FESTIVAL_EX("해외 페스티벌");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
