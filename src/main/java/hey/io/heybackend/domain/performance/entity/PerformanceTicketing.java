package hey.io.heybackend.domain.performance.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "\"performance_ticketing\"")
public class PerformanceTicketing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketingId;

    @Column(nullable = false)
    private Long performanceId;

    @Column(nullable = false, length = 20)
    private String ticketingBooth;

    @Column(length = 40)
    private String ticketingPremium;

    @Column
    private LocalDateTime openDatetime;

    @Column(length = 255)
    private String ticketingUrl;

}
