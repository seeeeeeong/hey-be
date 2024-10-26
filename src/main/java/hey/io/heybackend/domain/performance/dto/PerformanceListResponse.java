package hey.io.heybackend.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PerformanceListResponse {

    @Schema(description = "공연 ID", example = "1")
    private Long performanceId; // 공연의 고유 ID

    @Schema(description = "공연 명", example = "문학 콘서트 [과천]")
    private String performanceName; // 공연의 이름

    @Schema(description = "티켓 오픈 시간", example = "2024-10-01T00:00")
    private LocalDateTime openDateTime; // 티켓 판매 시작 시간

    @Schema(description = "티켓 상태", nullable = true,
            example = "READY",
            allowableValues = {"READY", "ONGOING", "CLOSED"})
    private TicketStatus ticketStatus; // 티켓의 현재 상태

    @Schema(description = "공연 시작 일자", example = "2024-10-05")
    private LocalDate startDate; // 공연 시작 날짜

    @Schema(description = "공연 종료 일자", example = "2024-10-05")
    private LocalDate endDate; // 공연 종료 날짜

    @Schema(description = "장소 명", example = "과천시민회관")
    private String placeName; // 공연이 열리는 장소의 이름

    @Schema(description = "파일 정보", example = "[{\"fileId\": 1, \"fileName\": \"example.png\", \"fileUrl\": \"http://example.com/image.png\"}]")
    private List<FileDTO> files; // 공연 관련 파일 리스트
}
