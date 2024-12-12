package hey.io.heybackend.domain.auth.service;

import hey.io.heybackend.domain.auth.dto.AuthDto;
import hey.io.heybackend.domain.auth.entity.Auth;
import hey.io.heybackend.domain.auth.enums.AuthType;
import hey.io.heybackend.domain.auth.repository.AuthRepository;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.user.entity.UserAuth;
import hey.io.heybackend.domain.user.repository.UserAuthRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final AuthRepository authRepository;

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return List 구조로 계층화한 권한 목록
     */
    public List<AuthDto> getAllHierarchicalAuthList() {
        return authRepository.selectHierarchicalAuthList();
    }

    /**
     * <p>유저 권한 저장</p>
     *
     * @param member 회원 정보
     */
    public void insertUserAuth(Member member) {
        List<Auth> auths = authRepository.findByAuthIdIn(List.of(AuthType.MEMBER_SNS.getCode(), AuthType.IS_AUTHENTICATED_FULLY.getCode()));
        List<UserAuth> userAuths = auths.stream()
            .map(auth -> UserAuth.of(member.getMemberId().toString(), auth))
            .toList();

        userAuthRepository.saveAll(userAuths);
    }
}
