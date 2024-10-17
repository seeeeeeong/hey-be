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
    @Column(name = "ticketing_id")
    private Long ticketingId;

    @Column(name = "performance_id", nullable = false)
    private Long performanceId;

    @Column(name = "ticketing_booth", nullable = false, length = 20)
    private String ticketingBooth;

    @Column(name = "ticketing_premium", length = 40)
    private String ticketingPremium;

    @Column(name = "open_datetime")
    private LocalDateTime openDatetime;

    @Column(name = "ticketing_url", length = 255)
    private String ticketingUrl;

}
