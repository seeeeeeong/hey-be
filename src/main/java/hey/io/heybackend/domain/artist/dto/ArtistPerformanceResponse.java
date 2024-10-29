package hey.io.heybackend.domain.artist.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.entity.Performance;
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

    @Schema(description = "티켓 오픈 시간", example = "2024.10.01 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime openDateTime;

    @Schema(description = "티켓 상태", example = "판매 종료")
    private String ticketStatus;

    @Schema(description = "공연 시작 일자", example = "2024.10.09")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate startDate;

    @Schema(description = "공연 종료 일자", example = "2024.10.09")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate endDate;

    @Schema(description = "장소 명", example = "과천시민회관")
    private String placeName;

    @Schema(description = "팔로우 여부", example = "true")
    private boolean isFollow = false;

    @Schema(description = "파일 정보 리스트",
            example = "[{\"fileId\": 1, \"fileCategory\": \"THUMBNAIL\", \"fileName\": \"example.png\", \"fileUrl\": \"http://example.com/image.png\", \"width\": 640, \"height\": 640}]")    private List<FileDTO> files; // 아티스트의 파일 정보

    public static ArtistPerformanceResponse of(Performance performance, boolean isFollow, List<FileDTO> files) {
        return ArtistPerformanceResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceName(performance.getName())
                .openDateTime(performance.getTicketings().getFirst().getOpenDatetime())
                .ticketStatus(performance.getTicketStatus().getDescription())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .placeName(performance.getPlace().getName())
                .isFollow(isFollow)
                .files(files)
                .build();
    }


}
