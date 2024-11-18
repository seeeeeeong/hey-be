package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.dto.PerformanceDetailResDto;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformancePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformancePriceRepository extends JpaRepository<PerformancePrice, Long> {

    List<PerformanceDetailResDto.PerformancePriceDto> findPerformancePriceByPerformance(Performance performance);

}
