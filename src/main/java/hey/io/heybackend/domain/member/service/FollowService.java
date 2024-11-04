package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.jwt.dto.JwtTokenInfo;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public boolean checkExistFollow(JwtTokenInfo jwtTokenInfo, Long targetId, FollowType followType) {
        if (jwtTokenInfo == null || jwtTokenInfo.getMemberId() == null) return false;
        return followRepository.existsFollow(followType, targetId, jwtTokenInfo.getMemberId());
    }

}
