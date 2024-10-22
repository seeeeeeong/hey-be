package hey.io.heybackend.domain.performance.controller;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceArtistResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
import hey.io.heybackend.domain.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
@Tag(name = "1. Performance", description = "공연 관련 API")
public class PerformanceController {

    private final PerformanceService performanceService;


    @GetMapping
    @Operation(summary = "공연 목록", description = "공연 목록을 조회합니다.")
    public ResponseEntity<SliceResponse<PerformanceListResponse>> getPerformanceList(PerformanceFilterRequest request,
                                                                                     @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                     @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        SliceResponse<PerformanceListResponse> performanceListResponse = performanceService.getPerformanceList(request, size, page, direction);
        return ResponseEntity.status(HttpStatus.OK).body(performanceListResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "공연 상세", description = "공연 상세를 조회합니다.")
    public ResponseEntity<PerformanceDetailResponse> getPerformanceDetail(@PathVariable("id") Long performanceId) {
        PerformanceDetailResponse getArtistDetailResponse = performanceService.getPerformanceDetail(performanceId);
        return ResponseEntity.status(HttpStatus.OK).body(getArtistDetailResponse);
    }

    @GetMapping("/{id}/artists")
    @Operation(summary = "공연 아티스트 목록", description = "공연 아티스트 목록을 조회합니다.")
    public ResponseEntity<SliceResponse<PerformanceArtistResponse>> getPerformanceArtistList(@PathVariable("id") Long performanceId,
                                                                                             @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                             @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        SliceResponse<PerformanceArtistResponse> performanceArtistListResponse = performanceService.getPerformanceArtistList(performanceId, size, page, direction);
        return ResponseEntity.status(HttpStatus.OK).body(performanceArtistListResponse);
    }


}
