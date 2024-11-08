package hey.io.heybackend.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PerformanceDTO {

    @Schema(description = "공연 ID", example = "87")
    private Long performanceId;

    @Schema(
            description = "공연 유형",
            example = "국내 콘서트",
            allowableValues = {"국내 콘서트", "내한 콘서트", "페스티벌", "해외 페스티벌"}
    )
    private PerformanceType performanceType;

    @Schema(
            description = "공연 상태",
            example = "공연 예정",
            allowableValues = {"공연 등록", "공연 예정", "공연 중", "공연 종료", "공연 취소"}
    )
    private PerformanceStatus performanceStatus;

    @Schema(description = "공연명", example = "현대카드 Curated 95, 한로로 X 윤마치 X QWER")
    private String name;

    @Schema(description = "공연 영문명", example = "Hyundai Card Curated 95, Hanrolo X Yoonmachi X QWER")
    private String engName;

    @Schema(description = "공연 시작 일자", example = "2024.10.09")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate startDate;

    @Schema(description = "공연 종료 일자", example = "2024.10.09")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate endDate;

    @Schema(description = "공연 시간", example = "1시간 30분")
    private String runningTime;

    @Schema(description = "관람 연령", example = "만 7세 이상")
    private String viewingAge;

}
