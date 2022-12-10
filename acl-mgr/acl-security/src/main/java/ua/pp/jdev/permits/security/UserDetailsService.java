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
		// Create default Administrator account if it isn't yet
		if(optUser.isEmpty()) {
			UserDetailsImpl defaultAdmin = new UserDetailsImpl(adminUsername, encoder.encode(adminUsername),"admin@jdev.pp.ua", "", "", Role.ADMIN.getDisplay(), true);
			defaultAdmin.grantAuthority(Role.ADMIN);
			repository.save(defaultAdmin);
		}
		
		// TODO Remove this ASAP!!!
		
		String viewerUsername = Role.VIEWER.getName().toLowerCase();
		Optional<UserDetailsImpl> optViewer = repository.findByUsername(viewerUsername);
		// Create default Viewer account if it isn't yet
		if(optViewer.isEmpty()) {
			UserDetailsImpl defaultViewer = new UserDetailsImpl(viewerUsername, encoder.encode(viewerUsername),"viewer@jdev.pp.ua", "", "", Role.VIEWER.getDisplay(), true);
			defaultViewer.grantAuthority(Role.VIEWER);
			repository.save(defaultViewer);
		}
		
		String editorUsername = Role.EDITOR.getName().toLowerCase();
		Optional<UserDetailsImpl> optEditor = repository.findByUsername(editorUsername);
		// Create default Editor account if it isn't yet
		if(optEditor.isEmpty()) {
			UserDetailsImpl defaultEditor = new UserDetailsImpl(editorUsername, encoder.encode(editorUsername),"editor@jdev.pp.ua", "", "", Role.VIEWER.getDisplay(), true);
			defaultEditor.grantAuthority(Role.EDITOR);
			repository.save(defaultEditor);
		}
		
		String editor2Username = Role.EDITOR.getName().toLowerCase() + 2;
		Optional<UserDetailsImpl> optEditor2 = repository.findByUsername(editor2Username);
		// Create additional Editor account if it isn't yet
		if(optEditor2.isEmpty()) {
			UserDetailsImpl extraEditor = new UserDetailsImpl(editor2Username, encoder.encode(editor2Username),"editor2@jdev.pp.ua", "", "", Role.VIEWER.getDisplay(), true);
			extraEditor.grantAuthority(Role.EDITOR);
			repository.save(extraEditor);
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
