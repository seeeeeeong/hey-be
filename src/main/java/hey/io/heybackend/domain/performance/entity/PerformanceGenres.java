package hey.io.heybackend.domain.performance.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "\"performance_genres\"")
public class PerformanceGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long performanceId;

    @Column(nullable = false, length = 20)
    private String performanceGenre;

}
