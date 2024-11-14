package hey.io.heybackend.domain.system.entity;

import hey.io.heybackend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "system")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"member", "auth"})
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호


    @Column(name = "user_id")
    private String userId; // 사용자 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth; // 권한 엔티티


    @Builder
    public UserAuth(String userId, Auth auth) {
        this.userId = userId;
        this.auth = auth;
    }
}