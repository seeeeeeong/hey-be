package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceFilterRequest {

    private PerformanceType performanceType;
    private List<PerformanceGenre> performanceGenre = new ArrayList<>();
    private List<PerformanceStatus> performanceStatus = new ArrayList<>();
    private List<TicketStatus> ticketStatus = new ArrayList<>();

}
