package hey.io.heybackend.domain.performance.controller;

import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.common.swagger.ApiErrorCodes;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
@Tag(name = "1. Performance", description = "공연 관련 API")
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * <p>공연 목록 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request 공연 목록 필터
     * @param pageable 페이지
     * @return 공연 목록
     */
    @GetMapping
    @ApiErrorCodes({ErrorCode.PERFORMANCE_NOT_FOUND, ErrorCode.INVALID_INPUT_VALUE})
    @Operation(summary = "공연 목록 조회", description = "공연 목록을 조회합니다.")
    public ApiResponse<SliceResponse<PerformanceListResDto>> getPerformanceList(@AuthenticationPrincipal MemberDto memberDto,
                                                                                  PerformanceListReqDto request,
                                                                                  @ParameterObject Pageable pageable) {

        return ApiResponse.success(performanceService.getPerformanceList(memberDto, request, pageable));

    }

    /**
     * <p>공연 상세 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param performanceId 공연 ID
     * @return 공연 상세 정보
     */
    @GetMapping("/{id}")
    @ApiErrorCode(ErrorCode.PERFORMANCE_NOT_FOUND)
    @Operation(summary = "공연 상세 조회", description = "공연 상세 정보를 조회합니다.")
    public ApiResponse<PerformanceDetailResDto> getPerformanceDetail(@AuthenticationPrincipal MemberDto memberDto,
                                                                       @PathVariable("id") Long performanceId) {

        return ApiResponse.success(performanceService.getPerformanceDetail(memberDto, performanceId));
    }

}
