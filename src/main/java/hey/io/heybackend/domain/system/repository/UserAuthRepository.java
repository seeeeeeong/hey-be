package hey.io.heybackend.domain.system.repository;

import hey.io.heybackend.domain.system.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
}
