package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springdoc.core.annotations.ParameterObject;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "공연 목록 필터")
@ParameterObject
public class PerformanceFilterRequest {

    @Parameter(description = "공연 유형 (CONCERT_IN, CONCERT_EX, FESTIVAL_IN, FESTIVAL_EX)",
            schema = @Schema(description = "공연 유형"))
    private PerformanceType type;

    @Parameter(
            description = "공연 장르 (BALLAD, HIPHOP, RNB, EDM, INDIE, ROCK, JAZZ, IDOL, ETC)",
            schema = @Schema(type = "array", description = "공연 장르")
    )
    private List<PerformanceGenre> genres = new ArrayList<>();

    @Parameter(
            description = "공연 상태 (INIT, READY, ONGOING, CLOSED, CANCEL)",
            schema = @Schema(type = "array", description = "공연 상태")
    )
    private List<PerformanceStatus> statuses = new ArrayList<>();

    @Parameter(
            description = "티켓 상태 (READY, ONGOING, CLOSED)",
            schema = @Schema(type = "array", description = "티켓 상태")
    )
    private List<TicketStatus> tickets = new ArrayList<>();
}
