package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceFilterRequest {


    private PerformanceType type; // 공연 유형
    private List<PerformanceGenre> genres = new ArrayList<>(); // 공연 장르
    private List<PerformanceStatus> statuses = new ArrayList<>(); // 공연 상태
    private List<TicketStatus> tickets = new ArrayList<>(); // 티켓 상태
}
