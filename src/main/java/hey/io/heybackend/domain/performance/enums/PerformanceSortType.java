package hey.io.heybackend.domain.performance.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceSortType implements EnumMapperType {
    LATEST_CREATED("등록일 기준 최신순"),
    LATEST_START("공연 시작일 기준 최신순");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
