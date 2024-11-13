package hey.io.heybackend.domain.system.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "system")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId; // 토큰 ID

    private Long memberId; // 회원 ID

    private String userId; // 사용자 ID

    @Column(nullable = false)
    private String refreshToken; // 리프레시 토큰

    @Builder
    public Token(Long memberId, String userId, String refreshToken) {
        this.memberId = memberId;
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}