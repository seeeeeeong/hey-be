package hey.io.heybackend.domain.member.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.member.dto.*;
import hey.io.heybackend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "4. Member", description = "회원 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {

    private final MemberService memberService;

    /**
     * <p>약관 동의 수정</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 회원 ID
     */
    @PutMapping("/member/terms")
    @ApiErrorCode(ErrorCode.MEMBER_NOT_FOUND)
    @Operation(summary = "약관 동의", description = "약관 동의 정보를 수정합니다.")
    public ApiResponse<Long> modifyMemberTerms(@AuthenticationPrincipal MemberDto memberDto,
                                                  @RequestBody @Valid MemberTermsReqDto request) {
        return ApiResponse.success(memberService.modifyMemberTerms(memberDto, request));
    }

    /**
     * <p>관심 정보 생성</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 회원 ID
     */
    @PostMapping("/member/interests")
    @ApiErrorCode(ErrorCode.MEMBER_NOT_FOUND)
    @Operation(summary = "관심 정보", description = "관심 정보를 생성합니다.")
    public ApiResponse<Long> createMemberInterest(@AuthenticationPrincipal MemberDto memberDto,
                                                  @RequestBody @Valid MemberInterestReqDto request) {
        return ApiResponse.created(memberService.createMemberInterest(memberDto, request));
    }

    /**
     * <p>회원 정보 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @return 회원 정보
     */
    @GetMapping("/mypage/info")
    @ApiErrorCode(ErrorCode.MEMBER_NOT_FOUND)
    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
    public ApiResponse<MemberInfoResDto> getMemberInfo(@AuthenticationPrincipal MemberDto memberDto) {
        return ApiResponse.success(memberService.getMemberInfo(memberDto));
    }

    /**
     * <p>닉네임 중복 확인</p>
     *
     * @param nickname
     * @return 닉네임 중복 여부
     */
    @GetMapping("/mypage/info/nickname")
    public ApiResponse<Boolean> existsNickname(@RequestParam("nickname") String nickname) {
        return ApiResponse.success(memberService.existsNickname(nickname));
    }

    /**
     * <p>회원 정보 수정</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 회원 ID
     */
    @PutMapping("/mypage/info")
    public ApiResponse<Long> modifyMember(@AuthenticationPrincipal MemberDto memberDto,
                                          @RequestBody @Valid ModifyMemberReqDto request) {
        return ApiResponse.success(memberService.modifyMember(memberDto, request));
    }

}
