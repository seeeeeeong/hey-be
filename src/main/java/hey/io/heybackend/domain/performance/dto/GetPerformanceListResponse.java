package hey.io.heybackend.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class GetPerformanceListResponse {

    private Long performanceId;
    private String performanceName;
    private LocalDateTime openDateTime;
    private TicketStatus ticketStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String placeName;

    @QueryProjection
    public GetPerformanceListResponse(Long performanceId, String performanceName, LocalDateTime openDateTime, TicketStatus ticketStatus, LocalDate startDate, LocalDate endDate, String placeName) {
        this.performanceId = performanceId;
        this.performanceName = performanceName;
        this.openDateTime = openDateTime;
        this.ticketStatus = ticketStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeName = placeName;
    }

}
