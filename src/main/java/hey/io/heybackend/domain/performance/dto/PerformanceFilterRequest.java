package hey.io.heybackend.domain.performance.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceFilterRequest {

    private String performanceType;
    private List<String> performanceGenre;
    private List<String> performanceStatus;
    private List<String> ticketStatus;

}
