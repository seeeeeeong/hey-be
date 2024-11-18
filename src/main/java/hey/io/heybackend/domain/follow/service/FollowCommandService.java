package hey.io.heybackend.domain.follow.service;

import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.domain.follow.entity.Follow;
import hey.io.heybackend.domain.follow.enums.FollowType;
import hey.io.heybackend.domain.follow.repository.FollowRepository;
import hey.io.heybackend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowCommandService {

    private final FollowRepository followRepository;

    private final FollowQueryService followQueryService;

    public int insertFollow(Member member, FollowType followType, List<Long> followTargetIds) {
        followTargetIds.forEach(followTargetId -> {
            if (followQueryService.existsFollow(member.getMemberId(), followType, followTargetId)) {
                throw new BusinessException(ErrorCode.FOLLOW_ALREADY_EXIST);
            }
            followRepository.save(Follow.of(member, followType, followTargetId));
        });
        return followTargetIds.size();
    }

    public int deleteFollow(Member member, FollowType followType, List<Long> followTargetIds) {
        followTargetIds.forEach(followTargetId -> {
            if (!followQueryService.existsFollow(member.getMemberId(), followType, followTargetId)) {
                throw new BusinessException(ErrorCode.FOLLOW_NOT_FOUND);
            }
            Follow follow = followQueryService.getFollow(member.getMemberId(), followType, followTargetId);
            followRepository.delete(follow);
        });

        return followTargetIds.size();
    }
}
