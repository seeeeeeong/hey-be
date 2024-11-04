package hey.io.heybackend.domain.member.entity;

import hey.io.heybackend.domain.member.enums.PushType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPush {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private PushType pushType;
    private boolean pushEnabled;

    @Builder
    public MemberPush(Member member, PushType pushType, boolean pushEnabled) {
        this.member = member;
        this.pushType = pushType;
        this.pushEnabled = pushEnabled;
    }

}
