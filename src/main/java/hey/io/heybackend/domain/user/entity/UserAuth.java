package hey.io.heybackend.domain.user.entity;

import hey.io.heybackend.domain.auth.entity.Auth;
import hey.io.heybackend.domain.auth.enums.AuthId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "system")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"userId", "auth"})
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호


    @Column(name = "user_id")
    private String userId; // 사용자 엔티티

    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    @Column(name = "auth_id")
    private AuthId authId;

    @Builder
    public UserAuth(String userId, AuthId authId) {
        this.userId = userId;
        this.authId = authId;
    }

}