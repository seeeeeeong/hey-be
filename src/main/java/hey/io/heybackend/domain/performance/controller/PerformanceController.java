package hey.io.heybackend.domain.performance.controller;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
public class PerformanceController {

    private final PerformanceService performanceService;


    @GetMapping
    public ResponseEntity<SliceResponse<PerformanceListResponse>> getPerformanceList(PerformanceFilterRequest filter,
                                                                                     @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                     @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        SliceResponse<PerformanceListResponse> performanceListResponse = performanceService.getPerformanceList(filter, size, page, direction);
        return ResponseEntity.status(HttpStatus.OK).body(performanceListResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDetailResponse> getPerformanceDetail(@PathVariable("id") Long performanceId) {
        PerformanceDetailResponse performanceDetailResponse = performanceService.getPerformanceDetail(performanceId);
        return ResponseEntity.status(HttpStatus.OK).body(performanceDetailResponse);
    }

}
