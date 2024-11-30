package hey.io.heybackend.domain.auth.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.auth.dto.AuthDto;
import hey.io.heybackend.domain.auth.entity.Auth;
import hey.io.heybackend.domain.auth.entity.UserAuth;
import hey.io.heybackend.domain.auth.enums.AuthType;
import hey.io.heybackend.domain.auth.repository.AuthRepository;
import hey.io.heybackend.domain.auth.repository.UserAuthRepository;
import hey.io.heybackend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final AuthRepository authRepository;

    /**
     * <p>권한 목록</p>
     *
     * @param authIds 권한 ID 목록
     * @return 권한 목록 정보
     */
    public List<Auth> getAuthList(List<String> authIds) {
        return authRepository.findByAuthIdIn(authIds);
    }

    /**
     * <p>권한 저장</p>
     *
     * @param userId 회원 ID
     * @param auths 권한 목록
     */
    @Transactional
    public void insertUserAuth(String userId, List<Auth> auths) {
        List<UserAuth> userAuths = auths.stream()
                .map(auth -> UserAuth.of(userId, auth))
                .toList();
        userAuthRepository.saveAll(userAuths);
    }

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return List 구조로 계층화한 권한 목록
     */
    public List<AuthDto> getAllHierarchicalAuthList() {
        return authRepository.selectHierarchicalAuthList();
    }


}
