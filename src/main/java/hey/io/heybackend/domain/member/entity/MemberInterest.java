package hey.io.heybackend.domain.member.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.STRING)
    private InterestCategory interestCategory;

    @Column(nullable = false)
    private String interestCode;

    @Builder
    private MemberInterest(Member member, InterestCategory interestCategory, String interestCode) {
        this.member = member;
        this.interestCategory = interestCategory;
        this.interestCode = interestCode;
    }

    public static MemberInterest of(Member member, InterestCategory interestCategory, String interestCode) {
        return MemberInterest.builder()
                .member(member)
                .interestCategory(interestCategory)
                .interestCode(interestCode)
                .build();
    }

}
