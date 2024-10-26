package hey.io.heybackend.domain.performance.entity;

import hey.io.heybackend.domain.artist.entity.Artist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(schema = "performance")
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance; // 공연 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist; // 아티스트 엔티티

    // 공연-아티스트 매핑 정보 생성
    public static PerformanceArtist of(Performance performance, Artist artist) {
        PerformanceArtist performanceArtist = new PerformanceArtist();
        performanceArtist.setPerformance(performance);
        performanceArtist.setArtist(artist);
        return performanceArtist;
    }
}
