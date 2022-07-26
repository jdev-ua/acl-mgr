package ua.pp.jdev.permits.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import ua.pp.jdev.permits.enums.State;

@Data
public class AccessControlList implements Cloneable {
	private Long id = Long.valueOf(0);

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "{validation.pattern.name}")
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
		Accessor result = new Accessor(State.NEW);
		result.setName(name);
		result.setAlias(alias);
		result.setSvc(svc);
		result.setPermit(permit);
		return result;
	}

	@Override
	public AccessControlList clone() throws CloneNotSupportedException {
		AccessControlList clone = (AccessControlList) super.clone();
		clone.objTypes = new HashSet<>(getObjTypes());
		clone.statuses = new HashSet<>(getStatuses());
		clone.accessors = new HashMap<>();
		// Deep clone contained accessors
		clone.setAccessors(getAccessors().stream().map(accessor -> {
			try {
				return accessor.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toSet()));

		return clone;
	}

	public Collection<Accessor> getAccessors() {
		return accessors.values();
	}

	public void setAccessors(Set<Accessor> newAccessors) {
		if (newAccessors != null) {
			newAccessors.forEach(accessor -> addAccessor(accessor));
		}
	}

	public void addAccessor(Accessor accessor) {
		if (accessor != null) {
			accessors.put(accessor.getName(), accessor);
		}
	}

	public boolean hasAccessor(String accessorName) {
		return accessors.containsKey(accessorName);
	}

	public Accessor getAccessor(String accessorName) {
		return accessors.get(accessorName);
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
