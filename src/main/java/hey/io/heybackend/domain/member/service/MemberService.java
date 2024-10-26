package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.domain.member.entity.Follow;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.repository.FollowRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

}
