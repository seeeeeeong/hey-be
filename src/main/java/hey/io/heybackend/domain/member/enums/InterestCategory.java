package hey.io.heybackend.domain.member.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterestCategory implements EnumMapperType {

    TYPE("공연 유형"),
    GENRE("공연 장르");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }

}
