package ua.pp.jdev.permits.security;

import java.util.Optional;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.enums.Role;

@Slf4j
@Configuration
public class UserConfig {
	@Bean
	protected ApplicationRunner userLoader(UserRepository repository, PasswordEncoder encoder) {
		log.debug("Start loading test users");
		
		return args -> {
			// Create 'admin' user
			createUser(repository, encoder, Role.ADMIN.getName().toLowerCase(), Role.ADMIN);
			// Create 'viewer' user
			createUser(repository, encoder, Role.VIEWER.getName().toLowerCase(), Role.VIEWER);
			// Create 'editor' user
			createUser(repository, encoder, Role.EDITOR.getName().toLowerCase(), Role.EDITOR);
			// Create 'editor2' user
			createUser(repository, encoder, Role.EDITOR.getName().toLowerCase() + 2, Role.EDITOR);
			
			log.debug("Finish loading test users");
		};
	}
	
	private void createUser(UserRepository repository, PasswordEncoder encoder, String userName, Role role) {
		Optional<UserDetailsImpl> optUser = repository.findByUsername(userName);
		if(optUser.isEmpty()) {
			UserDetailsImpl user = new UserDetailsImpl(userName, encoder.encode(userName),userName + "@example.com", "", "", role.getDisplay(), true);
			user.grantAuthority(role);
			repository.save(user);
			log.debug("User created succesfully: " + user.getUsername());
		}
	}

}
