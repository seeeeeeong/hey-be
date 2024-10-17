package hey.io.heybackend.domain.performance.service;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public SliceResponse<PerformanceListResponse> getPerformances(PerformanceFilterRequest filter, int size, int page, Sort.Direction direction) {

        Slice<PerformanceListResponse> performanceList = performanceRepository.getPerformances(filter, Pageable.ofSize(size).withPage(page), direction);
        return new SliceResponse<>(performanceList);

    }
}
