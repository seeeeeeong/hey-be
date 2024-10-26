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

    @Schema(description = "공연 유형", example = "CONCERT_IN",
            allowableValues = {"CONCERT_IN", "CONCERT_EX", "FESTIVAL_IN", "FESTIVAL_EX"})
    private PerformanceType type; // 공연 유형

    @Schema(description = "공연 장르", example = "[\"HIPHOP\", \"ROCK\"]",
            allowableValues = {"BALLAD", "HIPHOP", "RNB", "EDM", "INDIE", "ROCK", "JAZZ", "IDOL", "ETC"})
    private List<PerformanceGenre> genres = new ArrayList<>(); // 공연 장르 (ENUM 사용)

    @Schema(description = "공연 상태", example = "[\"ONGOING\", \"CLOSED\"]",
            allowableValues = {"INIT", "READY", "ONGOING", "CLOSED", "CANCEL"})
    private List<PerformanceStatus> statuses = new ArrayList<>(); // 공연 상태

    @Schema(description = "티켓 상태", example = "[\"READY\"]",
            allowableValues = {"READY", "ONGOING", "CLOSED"})
    private List<TicketStatus> tickets = new ArrayList<>(); // 티켓 상태
}
