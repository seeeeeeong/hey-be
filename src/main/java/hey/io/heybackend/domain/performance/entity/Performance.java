package hey.io.heybackend.domain.performance.entity;

import hey.io.heybackend.common.entity.BaseEntityWithUpdate;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "\"performance\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseEntityWithUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long performanceId;

    @Column(name = "performance_type", nullable = false, length = 20)
    private String performanceType;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "eng_name", length = 150)
    private String engName;

    @Column(name = "performance_uid", length = 8)
    private String performanceUid;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "place_id")
    private Long placeId; // FK

    @Column(name = "running_time", length = 20)
    private String runningTime;

    @Column(name = "viewing_age", length = 20)
    private String viewingAge;

    @Column(name = "ticket_status", nullable = false, length = 8, columnDefinition = "varchar(8) default 'READY'")
    private String ticketStatus = "READY";

    @Column(name = "performance_status", nullable = false, length = 8, columnDefinition = "varchar(8) default 'INIT'")
    private String performanceStatus = "INIT";


}
