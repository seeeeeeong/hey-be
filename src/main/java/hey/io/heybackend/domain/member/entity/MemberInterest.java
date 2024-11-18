package hey.io.heybackend.domain.member.entity;

import hey.io.heybackend.common.entity.BaseEntity;
import hey.io.heybackend.common.entity.BaseTimeEntity;
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
public class MemberInterest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String interestCategory;

    @Column(nullable = false)
    private String interestCode;

    @Builder
    private MemberInterest(Member member, String interestCategory, String interestCode) {
        this.member = member;
        this.interestCategory = interestCategory;
        this.interestCode = interestCode;
    }

    public static MemberInterest of(Member member, String interestCategory, String interestCode) {
        return MemberInterest.builder()
                .member(member)
                .interestCategory(interestCategory)
                .interestCode(interestCode)
                .build();
    }

}
