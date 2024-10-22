package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceArtistResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public SliceResponse<PerformanceListResponse> getPerformanceList(PerformanceFilterRequest request, int size, int page, Sort.Direction direction) {

        Slice<PerformanceListResponse> performanceList = performanceRepository.getPerformanceList(request, Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(performanceList);

    }

    public PerformanceDetailResponse getPerformanceDetail(Long performanceId) {

        Performance performance = performanceRepository.getPerformanceDetail(performanceId);

        return PerformanceDetailResponse.from(performance);
    }

    public SliceResponse<PerformanceArtistResponse> getPerformanceArtistList(Long performanceId, int size, int page, Sort.Direction direction) {

        Slice<PerformanceArtistResponse> performanceArtistListResponse = performanceRepository.getPerformanceArtistList(performanceId, Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(performanceArtistListResponse);

    }


}
