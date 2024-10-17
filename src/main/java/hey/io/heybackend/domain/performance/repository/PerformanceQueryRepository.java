package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;

import java.util.List;

public interface PerformanceQueryRepository {

    List<PerformanceListResponse> getPerformances(PerformanceFilterRequest filter);


}
