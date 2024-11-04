package hey.io.heybackend.domain.artist.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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


    @Builder
    public Artist(String name, String engName, String orgName, ArtistType artistType, String artistUid,
                  String artistUrl, Integer popularity, ArtistStatus artistStatus, List<File> artistFiles) {
        this.name = name;
        this.engName = engName;
        this.orgName = orgName;
        this.artistType = artistType;
        this.artistUid = artistUid;
        this.artistUrl = artistUrl;
        this.popularity = popularity;
        this.artistStatus = artistStatus;
        setArtistFiles(artistFiles);
    }

    // 이미지 정보 매핑
    private void setArtistFiles(List<File> files) {
        this.files = files;
    }

    }

