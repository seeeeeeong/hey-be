package hey.io.heybackend.domain.system.service;

import hey.io.heybackend.domain.system.dto.AuthDTO;
import hey.io.heybackend.domain.system.repository.AuthRepository;
import hey.io.heybackend.domain.system.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return List 구조로 계층화한 권한 목록
     */
    public List<AuthDTO> getAllHierarchicalAuthList() {
        return authRepository.selectHierarchicalAuthList();
    }
}
