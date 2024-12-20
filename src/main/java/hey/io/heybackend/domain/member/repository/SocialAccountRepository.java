package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.domain.member.entity.SocialAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    SocialAccount findByProviderUid(String providerUid);
}
