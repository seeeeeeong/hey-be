package hey.io.heybackend.common.config.component;

import hey.io.heybackend.domain.auth.dto.AuthDto;
import hey.io.heybackend.domain.auth.service.AuthService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AvailableRoleHierarchy {

    private RoleHierarchyImpl roleHierarchy;
    private final AuthService authService;

    /**
     * <p>AvailableRoleHierarchy - AuthService Injection</p>
     *
     * @param authService authService 클래스
     */
    public AvailableRoleHierarchy(AuthService authService) {
        this.authService = authService;
    }

    /**
     * <p>서버 최초 구동 시 권한 계층 설정</p>
     */
    @PostConstruct
    public void initRoleHierarchy() {
        roleHierarchy = buildRoleHierarchy();

        if (log.isInfoEnabled()) {
            log.info("Role's Hierarchy Initialized.");
        }
    }

    /**
     * <p>하위 계층 권한 포함 조회</p>
     *
     * @param authorities 조회할 권한 목록
     * @return 하위 계층 권한을 포함한 GrantedAuthority 목록
     */
    public List<GrantedAuthority> getReachableAuthorities(List<GrantedAuthority> authorities) {
        return (List<GrantedAuthority>) roleHierarchy.getReachableGrantedAuthorities(authorities);
    }

    /**
     * <p>권한 정보 변경 시 Reload</p>
     */
    public void reload() {
        roleHierarchy = buildRoleHierarchy();

        if (log.isInfoEnabled()) {
            log.info("Role's Hierarchy Reloaded.");
        }
    }

    /**
     * <p>RoleHierarchy 권한 빌드</p>
     *
     * @return RoleHierarchyImpl 형태의 권한 계층 정보
     */
    private RoleHierarchyImpl buildRoleHierarchy() {
        List<AuthDto> authList = authService.getAllHierarchicalAuthList();

        Builder builder = RoleHierarchyImpl.withRolePrefix("");
        Map<String, List<String>> roleHierarchyMap = new HashMap<>();

        // 권한 계층을 맵으로 수집
        for (AuthDto dto : authList) {
            roleHierarchyMap.computeIfAbsent(dto.getUpperAuthId(), key -> new ArrayList<>()).add(dto.getAuthId());
        }

        // 각 최상위 권한에 대해 하위 권한을 설정
        roleHierarchyMap.forEach((upperRole, impliedRoles) -> {
            builder.role(upperRole).implies(impliedRoles.toArray(new String[0]));
        });

        return builder.build();
    }
}

