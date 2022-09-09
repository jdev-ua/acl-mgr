package ua.pp.jdev.permits.data;

import java.util.Optional;

public interface AccessControlListDAO extends DataAccessObject<AccessControlList> {
	Optional<AccessControlList> readByName(String name);
}