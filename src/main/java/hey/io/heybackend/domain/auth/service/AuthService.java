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

    private final AuthRepository authRepository;
    private final UserAuthRepository userAuthRepository;

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return List 구조로 계층화한 권한 목록
     */
    public List<AuthDto> getAllHierarchicalAuthList() {
        return authRepository.selectHierarchicalAuthList();
    }

    @Transactional
    public void insertUserAuth(Member member) {
        Auth memberSnsAuth = authRepository.findById(AuthType.MEMBER_SNS.getCode()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.AUTH_NOT_FOUND));
        Auth isAuthenticatedFullyAuth = authRepository.findById(AuthType.IS_AUTHENTICATED_FULLY.getCode()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.AUTH_NOT_FOUND));

        String userId = String.valueOf(member.getMemberId());

        UserAuth memberSnsUserAuth = UserAuth.of(userId, memberSnsAuth);
        UserAuth IsAuthenticatedFullyUserAuth = UserAuth.of(userId, isAuthenticatedFullyAuth);

        userAuthRepository.save(memberSnsUserAuth);
        userAuthRepository.save(IsAuthenticatedFullyUserAuth);
    }
}
