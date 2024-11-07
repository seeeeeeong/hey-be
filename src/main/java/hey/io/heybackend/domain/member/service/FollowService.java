package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.jwt.dto.JwtTokenInfo;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.repository.FollowRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    public boolean checkExistFollow(JwtTokenInfo jwtTokenInfo, Long targetId, FollowType followType) {
        if (jwtTokenInfo == null || jwtTokenInfo.getMemberId() == null) return false;

        memberRepository.findById(jwtTokenInfo.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return followRepository.existsFollow(followType, targetId, jwtTokenInfo.getMemberId());
    }

}
