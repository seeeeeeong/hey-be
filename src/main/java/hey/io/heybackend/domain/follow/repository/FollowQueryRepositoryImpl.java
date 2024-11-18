package hey.io.heybackend.domain.follow.repository;

import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.follow.entity.Follow;
import hey.io.heybackend.domain.follow.enums.FollowType;

import java.util.List;

import static hey.io.heybackend.domain.follow.entity.QFollow.follow;


public class FollowQueryRepositoryImpl extends Querydsl5RepositorySupport implements FollowQueryRepository{

    public FollowQueryRepositoryImpl() {
        super(Follow.class);
    }

    @Override
    public Follow findFollow(Long memberId, FollowType followType, Long followTargetId) {
        return selectFrom(follow)
                .where(follow.followType.eq(followType), follow.followTargetId.eq(followTargetId), follow.member.memberId.eq(memberId))
                .fetchOne();
    }

    /**
     * <p>팔로우 여부</p>
     *
     * @param memberId
     * @param followType (PERFORMANCE, ARTIST)
     * @param followTargetId
     * @return boolean
     */
    @Override
    public boolean existsFollow(Long memberId, FollowType followType, Long followTargetId) {

        return selectFrom(follow)
                .where(follow.followType.eq(followType), follow.followTargetId.eq(followTargetId), follow.member.memberId.eq(memberId))
                .fetchFirst() != null;

    }

    /**
     * <p>팔로우 ID 목록 조회</p>
     *
     * @param memberId
     * @param followTargetIds
     * @return 팔로우 ID 리스트
     */
    @Override
    public List<Long> findFollowedTargetIds(Long memberId, FollowType followType, List<Long> followTargetIds) {
        return select(follow.followTargetId)
                .from(follow)
                .where(follow.member.memberId.eq(memberId), follow.followType.eq(followType), follow.followTargetId.in(followTargetIds))
                .fetch();
    }
}
