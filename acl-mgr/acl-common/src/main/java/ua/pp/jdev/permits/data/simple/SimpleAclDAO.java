package ua.pp.jdev.permits.data.simple;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Component
public class SimpleAclDAO implements AccessControlListDAO {
	private Map<String, AccessControlList> storage = new HashMap<>();

	@Override
	public Collection<AccessControlList> readAll() {
		// Disable direct access to stored data by getting every single ACL by
		// appropriate read method
		return storage.values().stream().map(acl -> read(String.valueOf(acl.getId())).get())
				.sorted(Comparator.comparing(AccessControlList::getName)).collect(Collectors.toList());
	}

	@Override
	public Optional<AccessControlList> read(String id) {
		Optional<AccessControlList> result;

		// Create a clone of current ACL to avoid unwanted changes by consumer
		AccessControlList acl = storage.get(id);
		if (acl != null) {
			result = Optional.of(AccessControlList.deepCopy(acl));
		} else {
			result = Optional.empty();
		}

		return result;
	}

	@Override
	public void create(AccessControlList newAcl) {
		if (newAcl != null) {
			newAcl.setId(IDGenerator.genStringID());
			newAcl.setAccessors(normilizeAccessors(newAcl.getAccessors()));
			storage.put(newAcl.getId(), newAcl);
		}
	}

	@Override
	public void update(AccessControlList data) {
		if (data != null && storage.containsKey(data.getId())) {
			AccessControlList acl = storage.get(data.getId());

			acl.setName(data.getName());
			acl.setDescription(data.getDescription());
			acl.setStatuses(data.getStatuses());
			acl.setObjTypes(data.getObjTypes());
			acl.setAccessors(normilizeAccessors(data.getAccessors()));
		}
	}

	private Set<Accessor> normilizeAccessors(Collection<Accessor> source) {
		// Purge Accessors in VOID state and setup others with PURE state
		return source.stream().filter(accessor -> !State.VOID.equals(accessor.getState()))
				.peek(accessor -> accessor.setState(State.PURE)).collect(Collectors.toSet());
	}

	@Override
	public boolean delete(String id) {
		return (storage.remove(id) != null);
	}

	@Override
	public Optional<AccessControlList> readByName(String name) {
		return storage.values().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst();
	}
}
