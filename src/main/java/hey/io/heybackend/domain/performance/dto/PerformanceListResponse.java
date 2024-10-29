package hey.io.heybackend.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.file.entity.File;
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
    private Long performanceId;

    @Schema(description = "공연 명", example = "문학 콘서트 [과천]")
    private String performanceName;

    @Schema(description = "티켓 오픈 시간", example = "2024.10.01 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime openDateTime;

    @Schema(description = "티켓 상태", nullable = true,
            example = "READY",
            allowableValues = {"READY", "ONGOING", "CLOSED"})
    private TicketStatus ticketStatus;

    @Schema(description = "공연 시작 일자", example = "2024.10.05")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate startDate;

    @Schema(description = "공연 종료 일자", example = "2024.10.05")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate endDate;

    @Schema(description = "장소명", example = "과천시민회관")
    private String placeName;

    @Schema(description = "팔로우 여부", example = "true")
    private boolean isFollow = false;

    @Schema(description = "파일 정보", example = "[{\"fileId\": 1, \"fileName\": \"example.png\", \"fileUrl\": \"http://example.com/image.png\"}]")
    private List<FileDTO> files;


    public static PerformanceListResponse of(Performance performance, List<FileDTO> fileList, boolean isFollow) {
        return PerformanceListResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceName(performance.getName())
                .openDateTime(performance.getTicketings().getFirst().getOpenDatetime())
                .ticketStatus(performance.getTicketStatus())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .placeName(performance.getPlace().getName())
                .isFollow(isFollow)
                .files(fileList)
                .build();
    }


}
