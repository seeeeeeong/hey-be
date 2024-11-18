package hey.io.heybackend.domain.artist.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "artist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artist extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artistId; // 아티스트 ID

    @Column(nullable = false)
    private String name; // 아티스트명

    private String engName; // 아티스트 영문명

    private String orgName; // 아티스트 본명

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtistType artistType; // 아티스트 유형

    private String artistUid; // Spotify 아티스트 ID

    private String artistUrl; // 아티스트 URL

    private Integer popularity; // 아티스트 인기도

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtistStatus artistStatus; // 아티스트 상태

    @Transient
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtistGenres> genres = new ArrayList<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceArtist> performanceArtists = new ArrayList<>();

}

