package hey.io.heybackend.domain.artist.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistPerformanceResponse {

    private Long performanceId;
    private String performanceName;
    private LocalDateTime openDateTime;
    private TicketStatus ticketStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String placeName;
    private List<FileDTO> files;


    @QueryProjection
    public ArtistPerformanceResponse(Long performanceId, String performanceName, LocalDateTime openDateTime, TicketStatus ticketStatus, LocalDate startDate, LocalDate endDate, String placeName) {
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
