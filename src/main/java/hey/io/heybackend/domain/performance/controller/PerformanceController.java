package hey.io.heybackend.domain.performance.controller;

import hey.io.heybackend.common.dto.SliceResponse;
import hey.io.heybackend.domain.performance.dto.GetPerformanceArtistListResponse;
import hey.io.heybackend.domain.performance.dto.GetPerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.GetPerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceFilterRequest;
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
    public ResponseEntity<SliceResponse<GetPerformanceListResponse>> getPerformanceList(PerformanceFilterRequest request,
                                                                                        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                        @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        SliceResponse<GetPerformanceListResponse> performanceListResponse = performanceService.getPerformanceList(request, size, page, direction);
        return ResponseEntity.status(HttpStatus.OK).body(performanceListResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPerformanceDetailResponse> getPerformanceDetail(@PathVariable("id") Long performanceId) {
        GetPerformanceDetailResponse getArtistDetailResponse = performanceService.getPerformanceDetail(performanceId);
        return ResponseEntity.status(HttpStatus.OK).body(getArtistDetailResponse);
    }

    @GetMapping("/{id}/artists")
    public ResponseEntity<SliceResponse<GetPerformanceArtistListResponse>> getPerformanceArtistList(@PathVariable("id") Long performanceId,
                                                                                                    @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                                                                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                                    @RequestParam(name = "direction", required = false, defaultValue = "DESC") Sort.Direction direction) {
        SliceResponse<GetPerformanceArtistListResponse> performanceArtistListResponse = performanceService.getPerformanceArtistList(performanceId, size, page, direction);
        return ResponseEntity.status(HttpStatus.OK).body(performanceArtistListResponse);
    }


}
