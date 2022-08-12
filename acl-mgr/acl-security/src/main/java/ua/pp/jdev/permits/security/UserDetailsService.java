package ua.pp.jdev.permits.security;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ua.pp.jdev.permits.enums.Role;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
	private UserRepository repository;

	public UserDetailsService(UserRepository userRepository) {
		repository = userRepository;
	}

	@PostConstruct
	private void populate() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		
		String adminUsername = Role.ADMIN.getName().toLowerCase();
		
		Optional<UserDetailsImpl> optUser = repository.findByUsername(adminUsername);
		// Create default administrator account if it isn't yet
		if(optUser.isEmpty()) {
			UserDetailsImpl defaultAdmin = new UserDetailsImpl(adminUsername, encoder.encode(adminUsername), "", "", Role.ADMIN.getDisplay(), true);
			defaultAdmin.grantAuthority(Role.ADMIN);
			repository.save(defaultAdmin);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserDetailsImpl> optUser = repository.findByUsername(username);
		if (optUser.isEmpty()) {
			// TODO Localize message!
			throw new UsernameNotFoundException("User '" + username + "' not found");
		}

		return optUser.get();
	}
}
