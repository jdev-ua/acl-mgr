package ua.pp.jdev.permits.data;

import java.util.Collection;
import java.util.Optional;

public interface UserDAO {
	Collection<User> readAll();

	Optional<User> read(String id);

	void create(User acl);

	void update(User acl);

	boolean delete(String id);
}
