package hey.io.heybackend.domain.follow.service;

import hey.io.heybackend.domain.follow.entity.Follow;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowQueryService {

    private final FollowRepository followRepository;

    public Follow getFollow(Long memberId, FollowType followType, Long followTargetId) {
        return followRepository.findFollow(memberId, FollowType.PERFORMANCE, followTargetId);
    }

    public Boolean existsFollow(Long memberId, FollowType followType, Long followTargetId) {
        return followRepository.existsFollow(memberId, followType, followTargetId);
    }

    public List<Long> getFollowedTargetIds(Long memberId, FollowType followType, List<Long> followTargetIds) {
        return followRepository.findFollowedTargetIds(memberId, followType, followTargetIds);
    }

}
