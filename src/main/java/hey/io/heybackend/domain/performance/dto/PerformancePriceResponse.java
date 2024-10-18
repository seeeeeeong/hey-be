package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.PerformancePrice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformancePriceResponse {

    private String priceInfo;
    private Integer priceAmount;

    public static PerformancePriceResponse from(PerformancePrice price) {
        return PerformancePriceResponse.builder()
                .priceInfo(price.getPriceInfo())
                .priceAmount(price.getPriceAmount())
                .build();
    }
}
