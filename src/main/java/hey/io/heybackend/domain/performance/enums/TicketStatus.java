package hey.io.heybackend.domain.performance.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketStatus implements EnumMapperType {
    READY("판매 예정"),
    ONGOING("판매 중"),
    CLOSED("판매 종료");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }

    // 공연 상태로 티켓 상태 조회 (추후 로직 변경)
    public static TicketStatus getByPerformanceStatus(PerformanceStatus performanceStatus) {
        return switch (performanceStatus) {
            case READY -> READY;
            case ONGOING -> ONGOING;
            case CLOSED -> CLOSED;
            default -> null;
        };
    }
}
