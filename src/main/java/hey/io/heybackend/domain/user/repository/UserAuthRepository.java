package hey.io.heybackend.domain.user.repository;

import hey.io.heybackend.domain.user.entity.UserAuth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    List<UserAuth> findByUserId(String userId);
}
