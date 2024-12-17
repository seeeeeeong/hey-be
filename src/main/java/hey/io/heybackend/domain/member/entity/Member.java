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
    private MemberStatus memberStatus;

    @Column(name = "basic_terms_agreed")
    private Boolean basicTermsAgreed;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;


    @Builder
    public Member(String email, String name, String nickname,
                   MemberStatus memberStatus, boolean basicTermsAgreed, LocalDateTime accessedAt) {
        this.email = email;
        this.name = (name != null) ? name : nickname;
        this.nickname = nickname;
        this.memberStatus = memberStatus;
        this.basicTermsAgreed = basicTermsAgreed;
        this.accessedAt = accessedAt;
    }

    // 회원 정보 업데이트
    public Member updateMember(String email, String name, MemberStatus memberStatus, Boolean basicTermsAgreed, LocalDateTime accessedAt) {
        this.email = email;
        this.name = name;
        this.memberStatus = memberStatus;
        this.basicTermsAgreed = basicTermsAgreed;
        this.accessedAt = accessedAt;
        return this;
    }

    // 닉네임 업데이트
    public void updateNickname(String newNickname) {
        if (this.name.equals(this.nickname)) {
            this.name = newNickname;
        }
        this.nickname = newNickname;
    }


    // 약관 동의 정보 업데이트
    public void setBasicTermsAgreed(Boolean basicTermsAgreed) {
        this.basicTermsAgreed = basicTermsAgreed;
    }

    // 회원 상태 업데이트
    public void setMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

}
