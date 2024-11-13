package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.member.entity.Follow;
import hey.io.heybackend.domain.member.enums.FollowType;

import static hey.io.heybackend.domain.member.entity.QFollow.follow;

public class FollowQueryRepositoryImpl extends Querydsl5RepositorySupport implements FollowQueryRepository{

    public FollowQueryRepositoryImpl() {
        super(Follow.class);
    }

    /**
     * <p>팔로우 조회</p>
     *
     * @return 팔로우 관계가 존재할 경우 true, 존재하지 않을 경우 false
     */
    @Override
    public boolean existsFollow(FollowType type, Long targetId, Long memberId) {

        return selectFrom(follow)
                .where(follow.followType.eq(type)
                        .and(follow.followTargetId.eq(targetId))
                        .and(follow.member.memberId.eq(memberId)))
                .fetchFirst() != null;

    }
}
