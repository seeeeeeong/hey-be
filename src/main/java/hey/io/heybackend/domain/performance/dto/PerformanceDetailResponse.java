package hey.io.heybackend.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import hey.io.heybackend.domain.performance.entity.*;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformanceDetailResponse {

    private Long performanceId;
    private String performanceName;
    private PerformanceType performanceType;
    private List<PerformanceGenreResponse> performanceGenres;
    private TicketStatus ticketStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String runningTime;
    private String viewingAge;
    private List<PerformancePriceResponse> performancePrices;
    private List<PerformanceTicketingResponse> performanceTicketings;
    private PlaceResponse place;

    public static PerformanceDetailResponse from(Performance performance, List<PerformanceGenres> genres,
                                                 List<PerformancePrice> prices, List<PerformanceTicketing> ticketings) {
        return PerformanceDetailResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceName(performance.getName())
                .performanceType(performance.getPerformanceType())
                .performanceGenres(genres.stream()
                        .map(PerformanceGenreResponse::from)
                        .toList())
                .ticketStatus(performance.getTicketStatus())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .runningTime(performance.getRunningTime())
                .viewingAge(performance.getViewingAge())
                .performancePrices(prices.stream()
                        .map(PerformancePriceResponse::from)
                        .toList())
                .performanceTicketings(ticketings.stream()
                        .map(PerformanceTicketingResponse::from)
                        .toList())
                .place(PlaceResponse.from(performance.getPlace()))
                .build();
    }

}
