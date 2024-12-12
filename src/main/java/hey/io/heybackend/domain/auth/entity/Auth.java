package hey.io.heybackend.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "system")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"authId"}, callSuper = false)
public class Auth {

    @Id
    private String authId; // 권한 ID

    @Column(nullable = false)
    private String name; // 권한명

    private String description; // 권한 설명

    @Column(nullable = false)
    private Integer authLevel; // 권한 수준

    @Column(nullable = false)
    private Integer authOrder; // 권한 순서

    @Column(nullable = false)
    private boolean enabled; // 사용 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upper_auth_id")
    private Auth upperAuth; // 상위 권한 엔티티

    @OneToMany(mappedBy = "upperAuth")
    private List<Auth> auths = new ArrayList<>(); // 하위 권한 엔티티

}