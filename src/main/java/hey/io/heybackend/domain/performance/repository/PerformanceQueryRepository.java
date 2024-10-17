package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PerformanceQueryRepository {

    Slice<PerformanceListResponse> getPerformances(PerformanceFilterRequest filter, Pageable pageable, Sort.Direction direction);


}
