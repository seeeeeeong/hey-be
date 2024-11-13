package hey.io.heybackend.domain.system.repository;



import hey.io.heybackend.domain.system.dto.AuthDTO;

import java.util.List;

public interface AuthQueryRepository {

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return 계층화 권한 목록
     */
    List<AuthDTO> selectHierarchicalAuthList();
}
