package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.member.entity.Follow;
import hey.io.heybackend.domain.member.enums.FollowType;

import static hey.io.heybackend.domain.member.entity.QFollow.follow;

public class FollowQueryRepositoryImpl extends Querydsl5RepositorySupport implements FollowQueryRepository{

    public FollowQueryRepositoryImpl() {
        super(Follow.class);
    }

    @Override
    public boolean existsFollow(FollowType type, Long targetId, Long memberId) {

        return selectFrom(follow)
                .where(follow.followType.eq(type)
                        .and(follow.followTargetId.eq(targetId))
                        .and(follow.member.memberId.eq(memberId)))
                .fetchFirst() != null;

    }
}
