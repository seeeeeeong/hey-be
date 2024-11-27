package hey.io.heybackend.domain.artist.entity;

import hey.io.heybackend.domain.artist.enums.ArtistGenre;
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
    private Artist artist; // 아티스트 엔티티

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtistGenre artistGenre; // 공연 장르
}
