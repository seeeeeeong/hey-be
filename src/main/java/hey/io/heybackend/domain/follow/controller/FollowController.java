package hey.io.heybackend.domain.follow.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;

import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.common.swagger.ApiErrorCodes;
import hey.io.heybackend.domain.artist.dto.ArtistListResDto;
import hey.io.heybackend.domain.follow.dto.FollowReqDto;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.follow.service.FollowService;
import hey.io.heybackend.domain.performance.dto.PerformanceListResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "3. Follow", description = "팔로우 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class FollowController {

    private final FollowService followService;

    /**
     * <p>팔로우</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 생성된 팔로우 수
     */
    @PostMapping("/mypage/follow")
    @Operation(summary = "팔로우 생성", description = "팔로우를 생성합니다.")
    @ApiErrorCodes({ErrorCode.FOLLOW_ALREADY_EXIST, ErrorCode.INVALID_INPUT_VALUE})
    public ApiResponse<Integer> createFollow(@AuthenticationPrincipal MemberDto memberDto,
                                             @RequestBody @Valid FollowReqDto request) {
        return ApiResponse.created(followService.createFollow(memberDto, request));
    }

    /**
     * <p>팔로우 취소</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 취소된 팔로우 수
     */
    @DeleteMapping("/mypage/follow")
    @Operation(summary = "팔로우 취소", description = "팔로우를 취소합니다.")
    @ApiErrorCodes({ErrorCode.FOLLOW_NOT_FOUND, ErrorCode.INVALID_INPUT_VALUE})
    public ApiResponse<Integer> removeFollow(@AuthenticationPrincipal MemberDto memberDto,
                                             @RequestBody @Valid FollowReqDto request) {
        return ApiResponse.success(followService.removeFollow(memberDto, request));
    }

    /**
     * <p>팔로우 공연 목록 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param pageable
     * @return 팔로우 공연 목록
     */
    @GetMapping("/mypage/follow/performances")
    @Operation(summary = "팔로우 공연 목록 조회", description = "팔로우 공연 목록을 조회합니다.")
    public ApiResponse<SliceResponse<PerformanceListResDto>> getFollowedPerformances(@AuthenticationPrincipal MemberDto memberDto,
                                                                                     @ParameterObject Pageable pageable) {
        return ApiResponse.success(followService.getFollowedPerformances(memberDto, pageable));
    }

    /**
     * <p>팔로우 아티스트 목록 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param pageable
     * @return 팔로우 아티스트 목록
     */
    @GetMapping("/mypage/follow/artists")
    @Operation(summary = "팔로우 아티스트 목록 조회", description = "팔로우 아티스트 목록을 조회합니다.")
    public ApiResponse<SliceResponse<ArtistListResDto>> getFollowedArtists(@AuthenticationPrincipal MemberDto memberDto,
                                                                           @ParameterObject Pageable pageable) {
        return ApiResponse.success(followService.getFollowedArtists(memberDto, pageable));
    }

}
