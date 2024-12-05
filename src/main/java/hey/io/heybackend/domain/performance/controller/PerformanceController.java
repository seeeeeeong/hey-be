package hey.io.heybackend.domain.performance.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.response.PageRequest;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceDetailResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceSearchCondition;
import hey.io.heybackend.domain.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
@Tag(name = "4. Performance", description = "공연 관련 API")
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * <p>공연 목록</p>
     *
     * @param searchCondition 조회 조건
     * @param tokenDto        토큰 정보
     * @param pageRequest     페이징 정보
     * @return 공연 목록
     */
    @GetMapping
    @Operation(summary = "공연 목록", description = "공연 목록을 조회합니다.")
    public ApiResponse<SliceResponse<PerformanceListResponse>> searchPerformanceList(
        @ParameterObject PerformanceSearchCondition searchCondition, @Parameter(hidden = true) TokenDto tokenDto,
        @Valid @ParameterObject PageRequest pageRequest) {
        return ApiResponse.success(
            performanceService.searchPerformanceSliceList(searchCondition, tokenDto, pageRequest));
    }

    /**
     * <p>공연 상세</p>
     *
     * @param performanceId 공연 ID
     * @param tokenDto      토큰 정보
     * @return 공연 상세 정보
     */
    @GetMapping("/{id}")
    @ApiErrorCode(ErrorCode.PERFORMANCE_NOT_FOUND)
    @Operation(summary = "공연 상세", description = "공연 상세 정보를 조회합니다.")
    public ApiResponse<PerformanceDetailResponse> getPerformanceDetail(@PathVariable("id") Long performanceId,
        @Parameter(hidden = true) TokenDto tokenDto) {
        return ApiResponse.success(performanceService.getPerformanceDetail(performanceId, tokenDto));
    }
}
