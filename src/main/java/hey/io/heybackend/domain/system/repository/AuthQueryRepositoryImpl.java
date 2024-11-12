package hey.io.heybackend.domain.system.repository;

import com.querydsl.core.types.Projections;
import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.system.dto.AuthDTO;
import hey.io.heybackend.domain.system.entity.Auth;

import java.util.List;

import static hey.io.heybackend.domain.system.entity.QAuth.auth;


public class AuthQueryRepositoryImpl extends Querydsl5RepositorySupport implements AuthQueryRepository {

    public AuthQueryRepositoryImpl() {
        super(Auth.class);
    }

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return 계층화 권한 목록
     */
    @Override
    public List<AuthDTO> selectHierarchicalAuthList() {
        return select(Projections.fields(AuthDTO.class,
            auth.authId,
            auth.upperAuth.authId.as("upperAuthId")))
            .from(auth)
            .where(auth.enabled.eq(true), auth.upperAuth.authId.isNotNull())
            .fetch();
    }
}
