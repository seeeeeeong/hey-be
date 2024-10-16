package hey.io.heybackend.domain.performance.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "\"performance\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId;

    @Column(nullable = false, length = 20)
    private String performanceType;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String engName;

    @Column(nullable = false, length = 8)
    private String performanceUid;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private Long placeId;

    @Column(length = 20)
    private String runningTime;

    @Column(length = 20)
    private String viewingAge;

    @Column(nullable = false, length = 8)
    private String ticketStatus = "READY";

    @Column(nullable = false, length = 8)
    private String performanceStatus = "INIT";

}
