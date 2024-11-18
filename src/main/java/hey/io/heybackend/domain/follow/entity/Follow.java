package hey.io.heybackend.domain.follow.entity;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.follow.enums.FollowType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_type")
    private FollowType followType;

    @Column(name = "follow_target_id")
    private Long followTargetId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    private Follow(Member member, FollowType followType, Long followTargetId) {
        this.member = member;
        this.followType = followType;
        this.followTargetId = followTargetId;
    }

    public static Follow of(Member member, FollowType followType, Long followTargetId) {
        return Follow.builder()
                .member(member)
                .followType(followType)
                .followTargetId(followTargetId)
                .build();
    }

}
