package hey.io.heybackend.domain.performance.controller;

import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
public class PerformanceController {

    private final PerformanceService performanceService;

//    @GetMapping
//    public ResponseEntity<List<PerformanceListResponse>> getPerformances() {
//        List<PerformanceListResponse> performanceListResponse = performanceService.getPerformances();
//        return ResponseEntity.status(HttpStatus.OK).body(performanceListResponse);
//    }

}
