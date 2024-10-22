package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.Place;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PerformanceDetailResponse {

    @Schema(description = "공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "공연 명", example = "문학 콘서트 [과천]")
    private String performanceName;

    @Schema(description = "공연 종료 일자", example = "2024-10-05")
    private PerformanceType performanceType;

    @Schema(description = "공연 장르 리스트", type = "array", example = "[{\"genreName\": \"발라드\"}, {\"genreName\": \"힙합\"}, {\"genreName\": \"R&B\"}, {\"genreName\": \"EDM\"}, {\"genreName\": \"인디\"}, {\"genreName\": \"락\"}, {\"genreName\": \"재즈\"}, {\"genreName\": \"아이돌\"}, {\"genreName\": \"기타\"}]")
    private List<PerformanceGenreElement> genres;

    @Schema(description = "티켓 상태", nullable = true, example = "[\"READY\", \"ONGOING\"], \"CLOSED\"]")
    private TicketStatus ticketStatus;

    @Schema(description = "공연 시작 일자", example =  "2024-10-05")
    private LocalDate startDate;

    @Schema(description = "공연 종료 일자", example = "2024-10-05")
    private LocalDate endDate;

    @Schema(description = "공연 시간", example = "1시간 10분")
    private String runningTime;

    @Schema(description = "관람 연령", example = "전체 관람가")
    private String viewingAge;

    @Schema(description = "가격 리스트", type = "array", example = "[{\"priceInfo\": \"전석\", \"priceAmount\": 30000}]")
    private List<PerformancePriceElement> prices;

    @Schema(description = "티켓팅 정보 리스트", type = "array", example = "[{\"ticketingBooth\": \"인터파크\", \"ticketingPremium\": null, \"openDatetime\": null, \"ticketingUrl\": \"http://ticket.interpark.com/Ticket/Goods/GoodsInfo.asp?GoodsCode=24014460\"}]")
    private List<PerformanceTicketingElement> ticketings;


    @Schema(description = "장소 명", example = "과천시민회관")
    private String placeName;

    @Schema(description = "주소", example = "경기도 과천시 통영로 5 (중앙동)")
    private String address;

    @Schema(description = "위도", example = "37.4279091")
    private double latitude;

    @Schema(description = "경도", example = "126.9895045")
    private double longitude;

    public static PerformanceDetailResponse from(Performance performance) {

        List<PerformanceGenreElement> genres = findGenres(performance);
        List<PerformancePriceElement> prices = findPrices(performance);
        List<PerformanceTicketingElement> ticketings = findTicketings(performance);

        Place place = performance.getPlace();

        return PerformanceDetailResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceName(performance.getName())
                .genres(genres)
                .ticketStatus(performance.getTicketStatus())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .runningTime(performance.getRunningTime())
                .viewingAge(performance.getViewingAge())
                .prices(prices)
                .ticketings(ticketings)
                .placeName(place.getName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }

    private static List<PerformanceGenreElement> findGenres(Performance performance) {
        return performance.getGenres().stream()
                .map(genre -> PerformanceGenreElement.from(genre))
                .collect(Collectors.toList());
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
