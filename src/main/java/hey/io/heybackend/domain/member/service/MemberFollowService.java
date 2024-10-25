package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.domain.member.entity.Follow;
import hey.io.heybackend.domain.member.enums.FollowType;
import hey.io.heybackend.domain.member.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFollowService {

    private final FollowRepository followRepository;


    public boolean isFollowed(Long memberId, FollowType followType, Long followTargetId) {
        Optional<Follow> follow = followRepository.findByMember_MemberIdAndFollowTypeAndFollowTargetId(memberId, followType, followTargetId);
        return follow.isPresent();
    }
}
