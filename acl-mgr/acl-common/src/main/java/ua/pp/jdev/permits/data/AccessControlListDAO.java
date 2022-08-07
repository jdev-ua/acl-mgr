package ua.pp.jdev.permits.data;

import java.util.Collection;
import java.util.Optional;

public interface AccessControlListDAO {
	Collection<AccessControlList> readAll();

	Optional<AccessControlList> read(String id);

	void create(AccessControlList acl);

	void update(AccessControlList acl);

	boolean delete(String id);
}