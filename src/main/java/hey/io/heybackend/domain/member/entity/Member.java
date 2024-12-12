package hey.io.heybackend.domain.member.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false)
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    @Column(name = "optional_terms_agreed")
    private Boolean optionalTermsAgreed = true;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;


    @Builder
    private Member(String email, String name, String nickname,
                   MemberStatus memberStatus, boolean optionalTermsAgreed, LocalDateTime accessedAt) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.memberStatus = memberStatus;
        this.optionalTermsAgreed = optionalTermsAgreed;
        this.accessedAt = accessedAt;
    }

    public static Member of(String email, String name, String nickname) {
        return Member.builder()
                .email(email)
                .name(name != null ? name : nickname)
                .nickname(nickname)
                .memberStatus(MemberStatus.INIT)
                .optionalTermsAgreed(false)
                .build();
    }

    // 회원 정보 업데이트
    public void updateMember(String email, String name) {
        this.email = email;
        this.name = name;
        this.memberStatus = MemberStatus.ACTIVE;
        this.optionalTermsAgreed = true;
        this.accessedAt = LocalDateTime.now();
    }

    // 약관 동의 정보 업데이트
    public void updateOptionalTermsAgreed(Boolean optionalTermsAgreed) {
        this.optionalTermsAgreed = optionalTermsAgreed;
    }

    // 닉네임 업데이트
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // 회원 상태 업데이트
    public void updateMemberStatus() {
        if (this.optionalTermsAgreed) { // 약관 동의 정보가 true일 경우
            this.memberStatus = MemberStatus.ACTIVE;
        } else { // 약관 동의 정보가 false일 경우
            this.memberStatus = MemberStatus.INIT;
        }
    }
}
