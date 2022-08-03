package ua.pp.jdev.permits.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import lombok.Data;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
public class AccessControlList implements Cloneable {
	// private Long id;
	private String id;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "{validation.pattern.name}")
	private String name = "";

	@Length(max = 128, message = "{validation.length.description}")
	private String description = "";

	@Transient
	private State state = State.PURE;

	private Set<String> objTypes = new HashSet<>();
	private Set<String> statuses = new HashSet<>();
	private Set<Accessor> accessors = new HashSet<>();

	public AccessControlList() {
		this(State.PURE);
	}

	public AccessControlList(State state) {
		if (State.NEW.equals(state)) {
			setId(IDGenerator.genStringID());
		}

		// Add dm_owner with default setting
		addAccessor(create("dm_owner", false, false, 7, state));
		// Add dm_world with default setting
		addAccessor(create("dm_world", false, false, 3, state));

		setState(state);
	}

	protected Accessor create(String name, boolean alias, boolean svc, int permit, State state) {
		// Accessor result = new Accessor(State.NEW);
		Accessor result = new Accessor(state);
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
		clone.accessors = new HashSet<>();
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

	public void addAccessor(Accessor accessor) {
		if (accessor != null) {
			// Remove previous version of Accessor from ACL
			removeAccessor(accessor.getName());
			// Add new Accessor to ACL
			accessors.add(accessor);
		}
	}

	public boolean hasAccessor(String accessorName) {
		return getAccessor(name).isPresent();
	}

	public Optional<Accessor> getAccessor(String name) {
		return accessors.stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst();
	}

	public void removeAccessor(String name) {
		Optional<Accessor> optional = getAccessor(name);
		if (optional.isPresent()) {
			accessors.remove(optional.get());
		}
	}

	public Collection<Accessor> getAccessors() {
		// Return a list of contained Accessors ordered by their names
		return accessors.stream().sorted(Comparator.comparing(Accessor::getName)).collect(Collectors.toList());
	}

	public void setAccessors(Set<Accessor> newAccessors) {
		if (newAccessors != null) {
			// Remove all previous version of Accessors from ACL
			accessors.clear();
			// Add new Accessors to ACL
			accessors.addAll(newAccessors);
		}
	}
}
