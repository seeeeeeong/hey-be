package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialAccountService {

    private final SocialAccountRepository socialAccountRepository;

    public Optional<SocialAccount> getSocialAccount(String providerUid) {
        return socialAccountRepository.findByProviderUid(providerUid);
    }

    @Transactional
    public SocialAccount saveSocialAccount(SocialAccount socialAccount) {
        return socialAccountRepository.save(socialAccount);
    }

}
