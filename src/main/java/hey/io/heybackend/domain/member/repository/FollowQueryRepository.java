package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.domain.member.enums.FollowType;

public interface FollowQueryRepository {

    /**
     * <p>팔로우 조회</p>
     *
     * @return 팔로우 관계가 존재할 경우 true, 존재하지 않을 경우 false
     */
    boolean existsFollow(FollowType type, Long targetId, Long memberId);

}
