package hey.io.heybackend.domain.user.service;

import hey.io.heybackend.common.config.component.AvailableRoleHierarchy;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.auth.enums.AuthId;
import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.user.entity.UserAuth;
import hey.io.heybackend.domain.user.repository.UserAuthRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final UserAuthRepository userAuthRepository;

    private final AvailableRoleHierarchy availableRoleHierarchy;

    /**
     * <p>사용자 정보 조회</p>
     *
     * @param userId 사용자 아이디
     * @return MemberDto
     */
    @Override
    public AuthenticatedMember loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<GrantedAuthority> authorities = loadUserAuthorities(userId);

        return AuthenticatedMember.of(member, authorities);
    }

    /**
     * <p>사용자 권한 정보 조회</p>
     *
     * @param userId 사용자 아이디
     * @return 사용자 권한 목록
     */
    public List<GrantedAuthority> loadUserAuthorities(String userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 사용자별 권한 조회
        List<AuthId> userAuthList = memberRepository.selectUserAuthList(Long.valueOf(userId));
        userAuthList.forEach(authId -> authorities.add(new SimpleGrantedAuthority(authId.getCode())));

        // 연결된 모든 하위 계층 권한 포함
        return availableRoleHierarchy.getReachableAuthorities(authorities);
    }

    /**
     * <p>사용자 권한 목록 조회</p>
     *
     * @param member 사용자
     * @return 사용자 권한 목록
     */
    public List<SimpleGrantedAuthority> getAuthorities(Member member) {
        String userId = String.valueOf(member.getMemberId());
        List<UserAuth> userAuthList = userAuthRepository.findByUserId(userId);

        return userAuthList.stream()
            .map(userAuth -> new SimpleGrantedAuthority(userAuth.getAuthId().getCode()))
            .collect(Collectors.toList());
    }

}
