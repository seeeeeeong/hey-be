package hey.io.heybackend.domain.auth.dto;

import hey.io.heybackend.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Getter
@Builder
public class AuthenticatedMember implements UserDetails {

    private Long memberId; // 사용자 ID
    private String email; // 이메일
    private Set<GrantedAuthority> authorities; // 권한 목록

    public static AuthenticatedMember of(Member member, Collection<? extends GrantedAuthority> authorities) {
        return AuthenticatedMember.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .authorities(Collections.unmodifiableSet(sortAuthorities(authorities)))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.memberId.toString();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AuthenticatedMember dto)) {
            return false;
        }
        return (this.memberId != null || dto.getMemberId() == null)
                && (this.memberId == null || this.memberId.equals(dto.getMemberId()));
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority,
                    "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

        @Serial
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

}
