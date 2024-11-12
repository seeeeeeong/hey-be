package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.config.component.AvailableRoleHierarchy;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.member.dto.MemberDTO;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.enums.PushType;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import hey.io.heybackend.domain.system.entity.Auth;
import hey.io.heybackend.domain.system.entity.UserAuth;
import hey.io.heybackend.domain.system.repository.AuthRepository;
import hey.io.heybackend.domain.system.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberPushRepository memberPushRepository;
    private final UserAuthRepository userAuthRepository;
    private final AuthRepository authRepository;
    private final SocialAccountRepository socialAccountRepository;


    private final AvailableRoleHierarchy availableRoleHierarchy;

    @Override
    public MemberDTO loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<GrantedAuthority> authorities = loadUserAuthorities(userId);

        return MemberDTO.of(member, authorities);
    }


    public Member saveOrUpdateMember(String email, String name, Provider provider) {
        Optional<Member> optionalMember = memberRepository.findAllByEmail(email).stream()
                .filter(existingMember -> existingMember.getSocialAccounts().stream()
                        .anyMatch(socialAccount -> socialAccount.getProvider() == provider))
                .findFirst();

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateMember(email, name != null ? name : member.getName());
            return memberRepository.save(member);
        } else {
            Member newMember = insertMember(email, name);
            insertMemberPush(newMember);
            insertUserAuth(newMember);
            return newMember;
        }
    }

    private Member insertMember(String email, String name) {
        String nickname = getNickname();
        Member newMember = Member.builder()
                .email(email)
                .name(name != null ? name : nickname)
                .nickname(nickname)
                .memberStatus(MemberStatus.ACTIVE)
                .optionalTermsAgreed(false)
                .accessedAt(LocalDateTime.now())
                .build();

        return memberRepository.save(newMember);
    }

    private String getNickname() {
        String nickname;
        do {
            String nicknameBase = "유저";
            int randomNumber = new Random().nextInt(100000);
            nickname = String.format("%s_%05d", nicknameBase, randomNumber);
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }

    private void insertMemberPush(Member member) {
        MemberPush memberPush = MemberPush.builder()
                .member(member)
                .pushType(PushType.PERFORMANCE)
                .pushEnabled(true)
                .build();

        memberPushRepository.save(memberPush);
    }

    private void insertUserAuth(Member member) {

        Auth authSns = authRepository.findById("MEMBER_SNS")
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.AUTH_NOT_FOUND));
        Auth authAuthenticated = authRepository.findById("IS_AUTHENTICATED_FULLY")
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.AUTH_NOT_FOUND));

        UserAuth userAuthSns = UserAuth.builder()
                .member(member)
                .auth(authSns)
                .build();

        UserAuth userAuthAuthenticated = UserAuth.builder()
                .member(member)
                .auth(authAuthenticated)
                .build();

        userAuthRepository.save(userAuthSns);
        userAuthRepository.save(userAuthAuthenticated);
    }

    public SocialAccount saveOrUpdateSocialAccount(Member member, Provider provider, String providerUid) {
        SocialAccount socialAccount = socialAccountRepository.findByMemberAndProvider(member, provider)
                .orElseGet(() -> insertSocialAccount(member, provider, providerUid));

        socialAccount.updateProviderUid(providerUid);
        return socialAccountRepository.save(socialAccount);
    }

    private SocialAccount insertSocialAccount(Member member, Provider provider, String providerUid) {
        SocialAccount socialAccount = SocialAccount.builder()
                .member(member)
                .provider(provider)
                .providerUid(providerUid)
                .build();

        return socialAccountRepository.save(socialAccount);
    }

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param userId 사용자 아이디
     * @return 사용자 권한 목록
     */
    public List<GrantedAuthority> loadUserAuthorities(String userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 사용자별 권한 조회
        List<String> userAuthList = memberRepository.selectUserAuthList(Long.valueOf(userId));
        userAuthList.forEach(authId -> authorities.add(new SimpleGrantedAuthority(authId)));

        // 연결된 모든 하위 계층 권한 포함
        return availableRoleHierarchy.getReachableAuthorities(authorities);
    }
}
