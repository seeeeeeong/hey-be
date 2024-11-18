package hey.io.heybackend.domain.artist.entity;

import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(schema = "artist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtistGenre artistGenre; // 공연 장르

}
