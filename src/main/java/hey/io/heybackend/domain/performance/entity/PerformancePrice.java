package hey.io.heybackend.domain.performance.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(schema = "performance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformancePrice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId; // 공연 가격 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance; // 공연 엔티티

    @Column(nullable = false)
    private String priceInfo; // 가격 설명

    @Column(nullable = false)
    private Integer priceAmount; // 금액

}
