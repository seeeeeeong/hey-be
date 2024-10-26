package hey.io.heybackend.domain.artist.dto;

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
public class ArtistPerformanceResponse {

    @Schema(description = "공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "공연 명", example = "문학 콘서트 [과천]")
    private String performanceName;

    @Schema(description = "티켓 오픈 시간", example = "2024-10-01T00:00")
    private LocalDateTime openDateTime;

    @Schema(description = "티켓 상태", example = "READY")
    private TicketStatus ticketStatus;

    @Schema(description = "공연 시작 일자", example = "2024-10-05")
    private LocalDate startDate;

    @Schema(description = "공연 종료 일자", example = "2024-10-05")
    private LocalDate endDate;

    @Schema(description = "장소 명", example = "과천시민회관")
    private String placeName;

    @Schema(description = "파일 정보", example = "[{\"fileId\": 1, \"fileName\": \"example.png\", \"fileUrl\": \"http://example.com/image.png\"}]")
    private List<FileDTO> files; // 아티스트의 파일 정보

}
