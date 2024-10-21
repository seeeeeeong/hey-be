package hey.io.heybackend.domain.artist.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(schema = "artist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(nullable = false)
    private hey.io.heybackend.domain.artist.enums.ArtistGenre artistGenre; // 공연 장르

}
