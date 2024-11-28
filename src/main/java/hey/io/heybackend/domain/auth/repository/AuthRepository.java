package hey.io.heybackend.domain.auth.repository;

import hey.io.heybackend.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthRepository extends JpaRepository<Auth, String>, AuthQueryRepository {
    List<Auth> findByAuthIdIn(List<String> authIds);
}
