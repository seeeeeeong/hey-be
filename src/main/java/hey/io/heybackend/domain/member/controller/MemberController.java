package hey.io.heybackend.domain.member.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.resolver.AuthUser;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import hey.io.heybackend.domain.member.dto.MemberInterestRequest;
import hey.io.heybackend.domain.member.dto.MemberTermsRequest;
import hey.io.heybackend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "1. Member", description = "회원 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {

  private final MemberService memberService;

  /**
   * <p>약관 동의 수정</p>
   *
   * @param authenticatedMember 인증 회원 정보
   * @param memberTermsRequest  약관 동의 정보
   * @return 회원 ID
   */
  @PutMapping("/member/terms")
  @ApiErrorCode(ErrorCode.MEMBER_NOT_FOUND)
  @Operation(summary = "약관 동의", description = "약관 동의 정보를 수정합니다.")
  public ApiResponse<Long> modifyMemberTerms(
      @AuthUser @Parameter(hidden = true) AuthenticatedMember authenticatedMember,
      @RequestBody @Valid MemberTermsRequest memberTermsRequest) {
    return ApiResponse.created(
        memberService.modifyMemberTerms(authenticatedMember, memberTermsRequest));
  }

  /**
   * <p>관심 정보 등록</p>
   *
   * @param authenticatedMember   인증 회원 정보
   * @param memberInterestRequest 관심 정보
   * @return 회원 ID
   */
  @PostMapping("/member/interests")
  @ApiErrorCode(ErrorCode.MEMBER_NOT_FOUND)
  @Operation(summary = "관심 정보", description = "관심 정보를 생성합니다.")
  public ApiResponse<Long> createMemberInterest(
      @AuthUser @Parameter(hidden = true) AuthenticatedMember authenticatedMember,
      @RequestBody @Valid MemberInterestRequest memberInterestRequest) {
    return ApiResponse.created(
        memberService.createMemberInterest(authenticatedMember, memberInterestRequest));
  }
}
