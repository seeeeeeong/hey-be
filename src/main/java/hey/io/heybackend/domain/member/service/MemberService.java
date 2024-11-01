package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Transactional
    public Member registerMember(String email, Provider provider, String providerUid) {
        String nickname = getNickname();
        Member member = memberRepository.save(Member.create(email, nickname));
        socialAccountRepository.save(SocialAccount.create(member, provider, providerUid));
        return member;
    }



    private String getNickname() {

        Random random = new Random();
        String nickname;

        do {
            String noun = "유저";
            String randomInt = String.valueOf(random.nextInt(999));
            nickname = MessageFormat.format("{0} {1}", noun, randomInt);
        } while (memberRepository.findByNickname(nickname).isPresent());

        return nickname;

    }




}
