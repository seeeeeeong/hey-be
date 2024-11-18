package hey.io.heybackend.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PerformanceListResDto {

    @Schema(description = "공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "공연명", example = "문학 콘서트 [과천]")
    private String performanceName;

    @Schema(description = "티켓 오픈 시간", example = "2024.10.01 00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime openDatetime;

    @Schema(description = "티켓 상태", example = "판매 예정")
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
    private Boolean isFollowed;

    @Schema(description = "파일 목록", nullable = true)
    private List<FileDto> files;

}
