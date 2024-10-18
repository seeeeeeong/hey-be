package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.PerformanceTicketing;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PerformanceTicketingResponse {

    private String ticketingBooth;  // 예매처
    private String ticketingPremium;  // 예매 프리미엄
    private LocalDateTime openDatetime;  // 티켓 오픈 시간
    private String ticketingUrl;  // 예매 링크

    public static PerformanceTicketingResponse from(PerformanceTicketing ticketing) {
        return PerformanceTicketingResponse.builder()
                .ticketingBooth(ticketing.getTicketingBooth())
                .ticketingPremium(ticketing.getTicketingPremium())
                .openDatetime(ticketing.getOpenDatetime())
                .ticketingUrl(ticketing.getTicketingUrl())
                .build();
    }
}
