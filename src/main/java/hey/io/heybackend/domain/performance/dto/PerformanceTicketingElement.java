package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.PerformanceTicketing;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PerformanceTicketingElement {

    private String ticketingBooth;  // 예매처
    private String ticketingPremium;  // 예매 프리미엄
    private LocalDateTime openDatetime;  // 티켓 오픈 시간
    private String ticketingUrl;  // 예매 링크

    public static PerformanceTicketingElement from(String ticketingBooth, String ticketingPremium,
                                                   LocalDateTime openDatetime, String ticketingUrl) {
        return PerformanceTicketingElement.builder()
                .ticketingBooth(ticketingBooth)
                .ticketingPremium(ticketingPremium)
                .openDatetime(openDatetime)
                .ticketingUrl(ticketingUrl)
                .build();
    }
}
