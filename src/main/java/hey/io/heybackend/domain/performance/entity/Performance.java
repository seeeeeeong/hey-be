package hey.io.heybackend.domain.performance.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(schema = "performance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId; // 공연 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PerformanceType performanceType; // 공연 유형

    @Column(nullable = false)
    private String name; // 공연명

    private String engName; // 공연 영문명

    private String performanceUid; // KOPIS 공연 ID

    @Column(nullable = false)
    private LocalDate startDate; // 공연 시작 일자

    @Column(nullable = false)
    private LocalDate endDate; // 공연 종료 일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place; // 장소 엔티티

    private String runningTime; // 공연 시간

    private String viewingAge; // 관람 연령

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus; // 티켓 상태

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus; // 공연 상태

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceGenres> genres = new ArrayList<>();

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformancePrice> prices = new ArrayList<>();

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceTicketing> ticketings = new ArrayList<>();

    @Transient
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceArtist> performanceArtists = new ArrayList<>();

    @Builder
    public Performance(PerformanceType performanceType, String name, String engName,
                       String performanceUid, LocalDate startDate, LocalDate endDate, Place place, String runningTime,
                       String viewingAge, TicketStatus ticketStatus, PerformanceStatus performanceStatus,
                       List<PerformancePrice> prices, List<PerformanceTicketing> ticketings, List<File> performanceFiles,
                       List<Artist> performanceArtists) {
        this.performanceType = performanceType;
        this.name = name;
        this.engName = engName;
        this.performanceUid = performanceUid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.runningTime = runningTime;
        this.viewingAge = viewingAge;
        this.ticketStatus = ticketStatus;
        this.performanceStatus = performanceStatus;
        setPrices(prices);
        setTicketings(ticketings);
        setPerformanceFiles(performanceFiles);
        setPerformanceArtists(performanceArtists);
    }

    // 가격 정보 매핑
    private void setPrices(List<PerformancePrice> prices) {
        this.prices = prices;
        prices.forEach(price -> price.updatePerformance(this));
    }

    // 예매 정보 매핑
    private void setTicketings(List<PerformanceTicketing> ticketings) {
        this.ticketings = ticketings;
        ticketings.forEach(ticketing -> ticketing.updatePerformance(this));
    }

    // 이미지 정보 매핑
    private void setPerformanceFiles(List<File> files) {
        this.files = files;
    }

    // 아티스트 정보 매핑
    private void setPerformanceArtists(List<Artist> artists) {
        removePerformanceArtist();
        artists.forEach(this::addPerformanceArtist);
    }

    // 기존 아티스트 제거
    private void removePerformanceArtist() {
        if (!this.performanceArtists.isEmpty()) {
            this.performanceArtists.clear();
        }
    }

    // 아티스트 매핑 정보 추가
    private void addPerformanceArtist(Artist artist) {
        PerformanceArtist performanceArtist = PerformanceArtist.of(this, artist);
        this.performanceArtists.add(performanceArtist);
    }


}

