package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.dto.PerformanceArtistResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

public interface PerformanceQueryRepository {

    Slice<PerformanceListResponse> getPerformanceList(PerformanceFilterRequest request, Pageable pageable, Sort.Direction direction);
    Slice<PerformanceArtistResponse> getPerformanceArtistList(Long performanceId, Pageable pageable, Sort.Direction direction);

}
