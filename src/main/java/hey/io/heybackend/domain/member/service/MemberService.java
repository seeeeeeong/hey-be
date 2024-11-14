package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.config.component.AvailableRoleHierarchy;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.member.dto.MemberDTO;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import hey.io.heybackend.domain.member.enums.NicknameType;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberPushRepository memberPushRepository;
    private final UserAuthRepository userAuthRepository;
    private final AuthRepository authRepository;
    private final SocialAccountRepository socialAccountRepository;


    private final AvailableRoleHierarchy availableRoleHierarchy;

    /**
     * <p>사용자 정보 조회</p>
     *
     * @param userId 사용자 아이디
     * @return 사용자 정보
     * @throws EntityNotFoundException throws UsernameNotFoundException
     */
    @Override
    public MemberDTO loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<GrantedAuthority> authorities = loadUserAuthorities(userId);

        return MemberDTO.of(member, authorities);
    }

    public List<SimpleGrantedAuthority> getAuthorities(Member member) {
        String userId = String.valueOf(member.getMemberId());
        List<UserAuth> userAuthList = userAuthRepository.findByUserId(userId);

        return userAuthList.stream()
                .map(userAuth -> new SimpleGrantedAuthority(userAuth.getAuth().getAuthId()))
                .collect(Collectors.toList());
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


    /**
     * <p>Member 저장/업데이트</p>
     *
     * @param email 사용자 이메일
     * @param name 사용자 이름
     * @param provider 소셜 로그인 provider
     * @return Member
     */
    public Member saveOrUpdateMember(String email, String name, Provider provider) {
        // email, provider를 통해 Member 조회
        Optional<Member> optionalMember = memberRepository.findAllByEmail(email).stream()
                .filter(existingMember -> existingMember.getSocialAccounts().stream()
                        .anyMatch(socialAccount -> socialAccount.getProvider() == provider))
                .findFirst();

        // Member가 존재하면 업데이트, 저장
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateMember(email, name != null ? name : member.getName());
            return memberRepository.save(member);
        // Member가 존재하지 않으면 생성, 저장
        } else {
            Member newMember = insertMember(email, name);
            insertMemberPush(newMember);
            insertUserAuth(newMember);
            return newMember;
        }
    }

    /**
     * <p>Member 저장</p>
     *
     * @param email 사용자 이메일
     * @param name 사용자 이름
     * @return Member
     */
    private Member insertMember(String email, String name) {
        // 랜덤 닉네임 생성
        String nickname = getNickname();

        Member newMember = Member.builder()
                .email(email)
                .name(name != null ? name : nickname) // 플랫폼 제공 name이 null인 경우, 닉네임 사용
                .nickname(nickname)
                .memberStatus(MemberStatus.ACTIVE)
                .optionalTermsAgreed(false) // 약관 동의 여부 false
                .accessedAt(LocalDateTime.now())
                .build();

        return memberRepository.save(newMember);
    }

    private String getNickname() {
        String nickname;
        do {
            String nicknameBase = NicknameType.getRandomNickname();
            int randomNumber = new Random().nextInt(100000);
            nickname = String.format("%s_%05d", nicknameBase, randomNumber);
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }


    /**
     * <p>MemberPush 저장</p>
     *
     * @return MemberPush
     */
    private void insertMemberPush(Member member) {
        MemberPush memberPush = MemberPush.builder()
                .member(member)
                .pushType(PushType.PERFORMANCE)
                .pushEnabled(true)
                .build();

        memberPushRepository.save(memberPush);
    }

    /**
     * <p>UserAuth 저장</p>
     *
     */
    private void insertUserAuth(Member member) {

        // MEMBER_SNS 권한 조회
        Auth authSns = authRepository.findById("MEMBER_SNS")
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.AUTH_NOT_FOUND));

        // IS_AUTHENTICATED_FULLY 권한 조회
        Auth authAuthenticated = authRepository.findById("IS_AUTHENTICATED_FULLY")
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.AUTH_NOT_FOUND));

        String userId = String.valueOf(member.getMemberId());

        UserAuth userAuthSns = UserAuth.builder()
                .userId(userId)
                .auth(authSns)
                .build();

        UserAuth userAuthAuthenticated = UserAuth.builder()
                .userId(userId)
                .auth(authAuthenticated)
                .build();

        userAuthRepository.save(userAuthSns);
        userAuthRepository.save(userAuthAuthenticated);
    }

    /**
     * <p>SocialAccount 저장/업데이트</p>
     *
     * @param member 회원 정보
     * @param provider 소셜 로그인 제공자
     * @param providerUid 제공자 UID
     * @return SocialAccount
     */
    public SocialAccount saveOrUpdateSocialAccount(Member member, Provider provider, String providerUid) {
        // SocialAccount 조회, 존재하지 않을 경우 저장
        SocialAccount socialAccount = socialAccountRepository.findByMemberAndProvider(member, provider)
                .orElseGet(() -> insertSocialAccount(member, provider, providerUid));

        // providerUid 업데이트
        socialAccount.updateProviderUid(providerUid);
        return socialAccountRepository.save(socialAccount);
    }

    /**
     * <p>SocialAccount 저장</p>
     *
     * @return SocialAccount
     */
    private SocialAccount insertSocialAccount(Member member, Provider provider, String providerUid) {
        SocialAccount socialAccount = SocialAccount.builder()
                .member(member)
                .provider(provider)
                .providerUid(providerUid)
                .build();

        return socialAccountRepository.save(socialAccount);
    }

}
