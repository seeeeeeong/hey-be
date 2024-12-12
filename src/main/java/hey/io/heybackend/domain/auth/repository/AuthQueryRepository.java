package hey.io.heybackend.domain.auth.repository;

import hey.io.heybackend.domain.auth.dto.AuthDto;
import java.util.List;

public interface AuthQueryRepository {

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return 계층화 권한 목록
     */
    List<AuthDto> selectHierarchicalAuthList();
}
