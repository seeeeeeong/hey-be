package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.PerformancePrice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformancePriceElement {

    private String priceInfo;
    private Integer priceAmount;

    public static PerformancePriceElement from(String priceInfo, Integer priceAmount) {
        return PerformancePriceElement.builder()
                .priceInfo(priceInfo)
                .priceAmount(priceAmount)
                .build();
    }
}
