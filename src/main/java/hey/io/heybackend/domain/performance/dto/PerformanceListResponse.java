package hey.io.heybackend.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceListResponse {

    @Schema(description = "공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "공연 명", example = "문학 콘서트 [과천]")
    private String performanceName;

    @Schema(description = "티켓 오픈 시간", example = "2024-10-01T00:00")
    private LocalDateTime openDateTime;

    @Schema(description = "티켓 상태", nullable = true, example = "[\"READY\", \"ONGOING\"], \"CLOSED\"]")
    private TicketStatus ticketStatus;

    @Schema(description = "공연 시작 일자", example =  "2024-10-05")
    private LocalDate startDate;

    @Schema(description = "공연 종료 일자", example = "2024-10-05")
    private LocalDate endDate;

    @Schema(description = "장소 명", example = "과천시민회관")
    private String placeName;

    private List<FileDTO> files;


    @QueryProjection
    public PerformanceListResponse(Long performanceId, String performanceName, LocalDateTime openDateTime, TicketStatus ticketStatus, LocalDate startDate, LocalDate endDate, String placeName) {
        this.performanceId = performanceId;
        this.performanceName = performanceName;
        this.openDateTime = openDateTime;
        this.ticketStatus = ticketStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeName = placeName;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }

}
