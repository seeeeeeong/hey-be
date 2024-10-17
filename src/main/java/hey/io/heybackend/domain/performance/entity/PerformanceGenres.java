package hey.io.heybackend.domain.performance.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "\"performance_genres\"")
public class PerformanceGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "performance_id", nullable = false)
    private Long performanceId;

    @Column(name = "performance_genre", nullable = false, length = 20)
    private String performanceGenre;

}
