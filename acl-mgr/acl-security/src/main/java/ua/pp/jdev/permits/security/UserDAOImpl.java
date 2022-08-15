package ua.pp.jdev.permits.security;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import ua.pp.jdev.permits.data.User;
import ua.pp.jdev.permits.data.UserDAO;
import ua.pp.jdev.permits.enums.Role;

@Component
public class UserDAOImpl implements UserDAO {
	UserRepository repository;
	PasswordEncoder encoder;

	public UserDAOImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.repository = userRepository;
		this.encoder = passwordEncoder;
	}

	@Override
	public Collection<User> readAll() {
		return Lists.newArrayList(repository.findAll()).stream().map(this::toUser)
				.sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());
	}

	@Override
	public Optional<User> read(String id) {
		Optional<UserDetailsImpl> result = repository.findById(Long.parseLong(id));
		return result.isPresent() ? Optional.of(this.toUser(result.get())) : Optional.empty();
	}

	@Override
	public void create(User user) {
		repository.save(fromUser(user));
	}

	@Override
	public void update(User user) {
		repository.save(fromUser(user));
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(Long.parseLong(id));
		return true;
	}

	protected User toUser(UserDetailsImpl userDetails) {
		User user = new User();

		user.setId(String.valueOf(userDetails.getId()));
		user.setUsername(userDetails.getUsername());
		user.setPassword(userDetails.getPassword());
		user.setEmail(userDetails.getEmail());
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setPosition(userDetails.getPosition());
		user.setEnabled(userDetails.isEnabled());
		user.setRoles(userDetails.getAuthorities().stream().map(t -> {
			Optional<Role> optRole = Role.getRole(t.getAuthority());
			if (optRole.isEmpty()) {
				// TODO Throw corresponding exception
				throw new RuntimeException();
			}
			return optRole.get();
		}).toList());
		user.setNew(false);

		return user;
	}

	protected UserDetailsImpl fromUser(User user) {
		UserDetailsImpl userDetails = new UserDetailsImpl(user.getUsername(), encoder.encode(user.getPassword()),
				user.getEmail(), user.getFirstName(), user.getLastName(), user.getPosition(), user.isEnabled());
		if (!user.isNew()) {
			userDetails.setId(Long.parseLong(user.getId()));
		}
		user.getRoles().forEach(t -> userDetails.grantAuthority(t));

		return userDetails;
	}
}
