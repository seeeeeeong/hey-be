package hey.io.heybackend.domain.performance.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceFilterRequest {

    private List<String> performanceGenre;
    private List<String> performanceStatus;
    private List<String> ticketStatus;

}
