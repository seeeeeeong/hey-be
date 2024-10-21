package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.Place;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetPerformanceDetailResponse {

    private Long performanceId;
    private String performanceName;
    private PerformanceType performanceType;
//    private PerformanceGenre genres;
    private TicketStatus ticketStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String runningTime;
    private String viewingAge;
    private List<PerformancePriceElement> prices;
    private List<PerformanceTicketingElement> ticketings;
    private String placeName;
    private String address;
    private double latitude;
    private double longitude;

    public static GetPerformanceDetailResponse from(Performance performance) {

        List<PerformancePriceElement> prices = findPrices(performance);
        List<PerformanceTicketingElement> ticketings = findTicketings(performance);


        return GetPerformanceDetailResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceName(performance.getName())
                .ticketStatus(performance.getTicketStatus())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .runningTime(performance.getRunningTime())
                .viewingAge(performance.getViewingAge())
                .prices(prices)
                .ticketings(ticketings)
                .build();
    }

    private static List<PerformancePriceElement> findPrices(Performance performance) {
        return performance.getPrices().stream()
                .map(price -> PerformancePriceElement.from(price.getPriceInfo(), price.getPriceAmount()))
                .collect(Collectors.toList());
    }

    private static List<PerformanceTicketingElement> findTicketings(Performance performance) {
        return performance.getTicketings().stream()
                .map(ticketing -> PerformanceTicketingElement.from(ticketing.getTicketingBooth(), ticketing.getTicketingPremium(), ticketing.getOpenDatetime(), ticketing.getTicketingUrl()))
                .collect(Collectors.toList());
    }

}
