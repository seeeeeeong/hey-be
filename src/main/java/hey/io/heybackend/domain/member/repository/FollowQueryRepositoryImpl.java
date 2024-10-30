package hey.io.heybackend.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heybackend.domain.member.enums.FollowType;
import lombok.RequiredArgsConstructor;

import static hey.io.heybackend.domain.member.entity.QFollow.follow;

@RequiredArgsConstructor
public class FollowQueryRepositoryImpl implements FollowQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsFollow(FollowType type, Long targetId, Long memberId) {

        return queryFactory.selectFrom(follow)
                .where(follow.followType.eq(type)
                        .and(follow.followTargetId.eq(targetId))
                        .and(follow.member.memberId.eq(memberId)))
                .fetchFirst() != null;

    }
}
