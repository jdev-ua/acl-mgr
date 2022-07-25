package ua.pp.jdev.permits.dao;

import java.util.Collection;
import java.util.Optional;

import ua.pp.jdev.permits.domain.AccessControlList;

public interface AccessControlListDAO {
	Collection<AccessControlList> readAll();

	Optional<AccessControlList> read(String id);

	void create(AccessControlList acl);

	void update(AccessControlList acl);

	Optional<AccessControlList> delete(String id);
}