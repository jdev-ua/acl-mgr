package ua.pp.jdev.permits.data;

import java.util.Optional;

public interface AclDAO extends DataAccessObject<Acl> {
	Optional<Acl> readByName(String name);
}