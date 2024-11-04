package hey.io.heybackend.domain.member.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();


    @Builder
    public Member(String email, String name, String nickname,
                   MemberStatus memberStatus, boolean optionalTermsAgreed, LocalDateTime accessedAt) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.memberStatus = memberStatus;
        this.optionalTermsAgreed = optionalTermsAgreed;
        this.accessedAt = accessedAt;
    }

    public void updateMember(String email, String name) {
        this.email = email;
        this.name = name;
        this.memberStatus = MemberStatus.ACTIVE;
        this.optionalTermsAgreed = true;
        this.accessedAt = LocalDateTime.now();
    }
}
