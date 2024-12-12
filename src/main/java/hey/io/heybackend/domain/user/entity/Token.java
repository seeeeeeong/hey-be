package hey.io.heybackend.domain.user.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(schema = "system")
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

    public static Token of(Long memberId, String refreshToken) {
        return Token.builder()
                .memberId(memberId)
                .refreshToken(refreshToken)
                .build();
    }

}