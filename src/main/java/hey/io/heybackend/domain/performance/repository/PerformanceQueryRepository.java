package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.dto.GetPerformanceArtistListResponse;
import hey.io.heybackend.domain.performance.dto.GetPerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

public interface PerformanceQueryRepository {

    Slice<GetPerformanceListResponse> getPerformanceList(PerformanceFilterRequest request, Pageable pageable, Sort.Direction direction);
    Slice<GetPerformanceArtistListResponse> getPerformanceArtistList(Long performanceId, Pageable pageable, Sort.Direction direction);

}
