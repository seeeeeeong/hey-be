package hey.io.heybackend.domain.user.entity;

import hey.io.heybackend.domain.auth.entity.Auth;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "system")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"user", "auth"})
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "auth_id", nullable = false)
    private String authId; // 권한 엔티티


    @Builder
    public UserAuth(String userId, String authId) {
        this.userId = userId;
        this.authId = authId;
    }
}