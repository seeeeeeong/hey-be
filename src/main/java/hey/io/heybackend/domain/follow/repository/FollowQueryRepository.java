package hey.io.heybackend.domain.follow.repository;

import hey.io.heybackend.domain.follow.entity.Follow;
import hey.io.heybackend.domain.follow.enums.FollowType;

import java.util.List;

public interface FollowQueryRepository {

    Follow findFollow(Long memberId, FollowType followType, Long followTargetId);

    /**
     * <p>팔로우 여부</p>
     *
     * @param memberId
     * @param followType (PERFORMANCE, ARTIST)
     * @param followTargetId
     * @return boolean
     */
    boolean existsFollow(Long memberId, FollowType followType, Long followTargetId);

    /**
     * <p>팔로우 ID 목록 조회</p>
     *
     * @param memberId
     * @param followTargetIds
     * @return 팔로우 ID 리스트
     */
    List<Long> findFollowedTargetIds(Long memberId, FollowType followType, List<Long> followTargetIds);

}
