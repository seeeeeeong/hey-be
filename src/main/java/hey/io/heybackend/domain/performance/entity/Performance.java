package hey.io.heybackend.domain.performance.entity;

import hey.io.heybackend.common.entity.BaseTimeEntity;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
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

}

