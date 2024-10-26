package hey.io.heybackend.domain.performance.entity;

import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(schema = "performance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance; // 공연 엔티티

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PerformanceGenre performanceGenre; // 공연 장르

}
