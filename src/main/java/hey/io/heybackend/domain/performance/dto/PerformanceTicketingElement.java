package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.PerformanceTicketing;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PerformanceTicketingElement {

    private String ticketingBooth;
    private String ticketingPremium;
    private LocalDateTime openDatetime;
    private String ticketingUrl;

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
