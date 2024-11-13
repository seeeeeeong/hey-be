package hey.io.heybackend.domain.system.repository;

import hey.io.heybackend.domain.system.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Auth, String>, AuthQueryRepository {

}
