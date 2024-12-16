package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.util.NicknameGenerator;
import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.login.dto.SocialUserInfo;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialAccountService {

  private final MemberRepository memberRepository;
  private final MemberPushRepository memberPushRepository;
  private final SocialAccountRepository socialAccountRepository;

  private final NicknameGenerator nicknameGenerator;
  private final AuthService authService;

  /**
   * <p>회원 및 소설 계정 저장/업데이트</p>
   *
   * @param socialUserInfo 소셜 사용자 정보
   * @return 회원 정보
   */
  public Member mergeMemberAndSocialAccount(SocialUserInfo socialUserInfo) {
    Member member = mergeMember(socialUserInfo);
    mergeSocialAccount(member, socialUserInfo);
    return member;
  }

  // 회원 저장/업데이트
  private Member mergeMember(SocialUserInfo socialUserInfo) {
    // 회원 정보 조회
    Optional<Member> existingMember = memberRepository.selectMemberByProviderUid(socialUserInfo.providerUid());

    if (existingMember.isPresent()) {
      // 회원 업데이트
      Member member = existingMember.get();
      member.updateMember(socialUserInfo.email(), socialUserInfo.name());
      return member;
    } else {
      // 회원 저장
      Member newMember = Member.of(socialUserInfo.email(), socialUserInfo.name(), generateNickname());
      memberRepository.save(newMember);

      // 푸시 알림 저장
      MemberPush memberPush = MemberPush.of(newMember);
      memberPushRepository.save(memberPush);

      // 권한 정보 저장
      authService.insertUserAuth(newMember);

      return newMember;
    }
  }

  // 소셜 계정 저장/업데이트
  private void mergeSocialAccount(Member member, SocialUserInfo socialUserInfo) {
    // 소설 계정 정보 조회
    Optional<SocialAccount> existingSocialAccount = socialAccountRepository.findByProviderUid(socialUserInfo.providerUid());

    if (existingSocialAccount.isPresent()) {
      // 소셜 정보 업데이트
      SocialAccount socialAccount = existingSocialAccount.get();
      socialAccount.updateSocialAccount(socialAccount.getProvider(), socialUserInfo.providerUid());
    } else {
      // 소셜 정보 저장
      SocialAccount socialAccount = SocialAccount.of(member, socialUserInfo.provider(), socialUserInfo.providerUid());
      socialAccountRepository.save(socialAccount);
    }
  }

  // 랜덤 닉네임 생성
  private String generateNickname() {
    String nickname;
    do {
      nickname = nicknameGenerator.generateNickname();
    } while (memberRepository.existsByNickname(nickname));
    return nickname;
  }

}
