package hey.io.heybackend.domain.mypage.controller;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.resolver.AuthUser;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.common.swagger.ApiErrorCode;
import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import hey.io.heybackend.domain.mypage.dto.MyPageDto.MemberDetailResponse;
import hey.io.heybackend.domain.mypage.dto.MyPageDto.ModifyMemberRequest;
import hey.io.heybackend.domain.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "6. MyPage", description = "마이페이지 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class MyPageController {

  private final MyPageService myPageService;

  /**
   * <p>회원 정보 조회</p>
   *
   * @param authenticatedMember 인증 회원 정보
   * @return 회원 정보
   */
  @GetMapping("/mypage/info")
  @ApiErrorCode(ErrorCode.MEMBER_NOT_FOUND)
  @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
  public ApiResponse<MemberDetailResponse> getMemberInfo(
      @AuthUser @Parameter(hidden = true) AuthenticatedMember authenticatedMember) {
    return ApiResponse.success(myPageService.getMemberInfo(authenticatedMember));
  }

  /**
   * <p>닉네임 중복 확인</p>
   *
   * @param nickname 닉네임
   * @return 닉네임 중복 여부
   */
  @GetMapping("/mypage/info/nickname")
  @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복을 확인합니다.")
  public ApiResponse<Boolean> existsNickname(@RequestParam("nickname") String nickname) {
    return ApiResponse.success(myPageService.existsNickname(nickname));
  }

  /**
   * <p>회원 정보 수정</p>
   *
   * @param authenticatedMember 인증 회원 정보
   * @param modifyMemberRequest 회원 정보
   * @return 회원 ID
   */
  @PutMapping("/mypage/info")
  @ApiErrorCode(ErrorCode.MEMBER_NOT_FOUND)
  @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
  public ApiResponse<Long> modifyMember(
      @AuthUser @Parameter(hidden = true) AuthenticatedMember authenticatedMember,
      @RequestBody @Valid ModifyMemberRequest modifyMemberRequest) {
    return ApiResponse.created(
        myPageService.modifyMember(authenticatedMember, modifyMemberRequest));
  }

}
