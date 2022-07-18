package ua.pp.jdev.permits.dao;

import java.util.Collection;

import ua.pp.jdev.permits.domain.AccessControlList;

public interface AccessControlListDAO {
	Collection<AccessControlList> readAll();

	AccessControlList read(String id);

	void create(AccessControlList acl);

	void update(AccessControlList acl);

	AccessControlList delete(String id);
}