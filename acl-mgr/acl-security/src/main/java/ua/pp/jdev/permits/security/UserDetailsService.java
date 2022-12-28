package ua.pp.jdev.permits.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
	private UserRepository repository;

	public UserDetailsService(UserRepository userRepository) {
		repository = userRepository;
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
