package hey.io.heybackend.domain.performance.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "\"place\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "place_uid", length = 8)
    private String placeUid;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "latitude", precision = 9, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 17, scale = 14)
    private BigDecimal longitude;

}
