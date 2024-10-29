package hey.io.heybackend.domain.performance.controller;

import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.JwtTokenInfo;
import hey.io.heybackend.common.resolver.AuthUser;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
@Tag(name = "1. Performance", description = "공연 관련 API")
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * <p>공연 목록을 조회합니다.</p>
     *
     * @param request 공연 목록 필터
     * @param pageable 페이지 정보
     * @return {@link SliceResponse} 객체, 공연 목록과 페이지 정보를 포함
     */
    @GetMapping
    @Operation(summary = "공연 목록", description = "공연 목록을 조회합니다.")
    public ApiResponse<SliceResponse<PerformanceListResponse>> getPerformanceList(@AuthUser JwtTokenInfo jwtTokenInfo,
                                                                                  PerformanceFilterRequest request,
                                                                                  Pageable pageable) {
        return ApiResponse.success(performanceService.getPerformanceList(jwtTokenInfo, request, pageable));
    }

    /**
     * <p>특정 공연의 상세 정보를 조회합니다.</p>
     *
     * @param performanceId 공연의 ID
     * @param jwtTokenInfo JWT 토큰 정보 (인증용)
     * @return {@link PerformanceDetailResponse} 객체, 공연 상세 정보를 포함
     * @throws BusinessException 공연을 찾을 수 없는 경우 {@link ErrorCode#PERFORMANCE_NOT_FOUND} 예외 발생
     */
    @GetMapping("/{id}")
    @ApiErrorCode(ErrorCode.PERFORMANCE_NOT_FOUND)
    @Operation(summary = "공연 상세", description = "공연 상세를 조회합니다.")
    public ApiResponse<PerformanceDetailResponse> getPerformanceDetail(@PathVariable("id") Long performanceId,
                                                                          @AuthUser JwtTokenInfo jwtTokenInfo) {

        return ApiResponse.success(performanceService.getPerformanceDetail(performanceId, jwtTokenInfo));
    }

}
