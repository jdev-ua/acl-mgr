package ua.pp.jdev.permits.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import ua.pp.jdev.permits.dao.IDGenerator;

@Data
public class AccessControlList {
	private String id = IDGenerator.NULL_ID;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	private String name = "";

	@Length(max = 128, message = "{validation.length.description}")
	private String description = "";

	private Set<String> objTypes = new HashSet<>();

	private Set<String> statuses = new HashSet<>();

	private Map<String, Accessor> accessors = new HashMap<>();

	public AccessControlList() {
		// Add dm_owner with default setting
		addAccessor(create("dm_owner", false, false, 7));
		// Add dm_world with default setting
		addAccessor(create("dm_world", false, false, 3));
	}

	private Accessor create(String name, boolean alias, boolean svc, int permit) {
		Accessor result = new Accessor();
		result.setName(name);
		result.setAlias(alias);
		result.setSvc(svc);
		result.setPermit(permit);
		return result;
	}

	public Collection<Accessor> getAccessors() {
		return accessors.values();
	}

	public void setAccessors(Set<Accessor> newAccessors) {
		// Clear actual list
		accessors.clear();

		if (newAccessors == null) {
			// Add dm_owner with default setting
			addAccessor(create("dm_owner", false, false, 7));
			// Add dm_world with default setting
			addAccessor(create("dm_world", false, false, 3));
		} else {
			// Add new accessors
			newAccessors.forEach(t -> addAccessor(t));

			// Add dm_owner with default setting if it is necessary
			if (!hasAccessor("dm_owner")) {
				addAccessor(create("dm_owner", false, false, 7));
			}

			// Add dm_owner with default setting if it is necessary
			if (!hasAccessor("dm_world")) {
				addAccessor(create("dm_world", false, false, 3));
			}
		}
	}

	public void addAccessor(Accessor accessor) {
		accessors.put(accessor.getName(), accessor);
	}

	public boolean hasAccessor(String accessorName) {
		return accessors.containsKey(accessorName);
	}

	public void removeAccessor(String accessorName) {
		accessors.remove(accessorName);
	}

	public void addStatus(String status) {
		statuses.add(status);
	}

	public void removeStatus(String status) {
		statuses.remove(status);
	}

	public void addObjType(String objType) {
		objTypes.add(objType);
	}

	public void removeObjType(String objType) {
		objTypes.remove(objType);
	}
}
