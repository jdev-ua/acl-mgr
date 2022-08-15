package ua.pp.jdev.permits.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ua.pp.jdev.permits.enums.Role;

@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity(name = "user_info")
class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 6016725156704752030L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique=true)
	private final String username;
	private final String password;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String position;
	private final boolean enabled;
	
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="user_roles", joinColumns = @JoinColumn(name="id"))
	private Set<String> roles = new HashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles().stream().map(SimpleGrantedAuthority::new).toList();
	}

	public void grantAuthority(Role authority) {
		roles.add(authority.getFullName());
	}

	public boolean revokeAuthority(Role role) {
		return roles.remove(role.getFullName());
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
}
