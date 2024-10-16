package hey.io.heybackend.domain.performance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PerformanceListResponse {

    private Long performanceId;
    private String performanceName;
    private LocalDateTime openDateTime;
    private String ticketStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String placeName;

}
