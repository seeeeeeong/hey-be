package hey.io.heybackend.domain.performance.mapper;

import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceGenres;
import hey.io.heybackend.domain.performance.entity.PerformancePrice;
import hey.io.heybackend.domain.performance.entity.PerformanceTicketing;
import hey.io.heybackend.domain.performance.repository.PerformanceGenresRepository;
import hey.io.heybackend.domain.performance.repository.PerformancePriceRepository;
import hey.io.heybackend.domain.performance.repository.PerformanceTicketingRepository;
import hey.io.heybackend.domain.performance.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PerformanceDetailMapper {

    private final PerformanceGenresRepository performanceGenresRepository;
    private final PerformancePriceRepository performancePriceRepository;
    private final PerformanceTicketingRepository performanceTicketingRepository;

    public PerformanceDetailResponse mapToPerformanceDetailResponse(Performance performance) {

        List<PerformanceGenres> performanceGenres = performanceGenresRepository.findByPerformance(performance);
        List<PerformancePrice> performancePrices = performancePriceRepository.findByPerformance(performance);
        List<PerformanceTicketing> performanceTicketings = performanceTicketingRepository.findByPerformance(performance);

        return PerformanceDetailResponse.from(
                performance,
                performanceGenres,
                performancePrices,
                performanceTicketings
        );

    }
}
