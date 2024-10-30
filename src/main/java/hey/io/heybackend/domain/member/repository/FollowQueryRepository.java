package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.domain.member.enums.FollowType;

public interface FollowQueryRepository {

    boolean existsFollow(FollowType type, Long targetId, Long memberId);

}
