package ua.pp.jdev.permits.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ua.pp.jdev.permits.dao.IDGenerator;
import ua.pp.jdev.permits.enums.State;

@Data
@Table("ACL")
public class AccessControlList implements Cloneable, Serializable, Persistable<Long> {
	private static final long serialVersionUID = -6820394109565967621L;

	@Id
	private Long id;

	@Transient
	private State state = State.PURE;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "{validation.pattern.name}")
	private String name = "";

	@Length(max = 128, message = "{validation.length.description}")
	private String description = "";

	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PACKAGE)
	@MappedCollection(idColumn = "ACL_ID")
	private Set<Status> rawStatuses = new HashSet<>();

	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PACKAGE)
	@MappedCollection(idColumn = "ACL_ID")
	private Set<ObjType> rawObjTypes = new HashSet<>();

	@MappedCollection(idColumn = "ACL_ID")
	private Set<Accessor> accessors = new HashSet<>();

	public AccessControlList() {
		this(State.PURE);
	}

	public AccessControlList(State state) {
		if (State.NEW.equals(state)) {
			setId(IDGenerator.generateID());
		}

		// Add dm_owner with default setting
		addAccessor(create("dm_owner", false, false, 7));
		// Add dm_world with default setting
		addAccessor(create("dm_world", false, false, 3));

		setState(state);
	}

	@PersistenceCreator
	protected AccessControlList(Long id, String name, String description, Set<Status> rawStatuses,
			Set<ObjType> rawObjTypes, Set<Accessor> accessors) {
		setName(name);
		setId(id);
		setDescription(description);
		setRawStatuses(rawStatuses);
		setRawObjTypes(rawObjTypes);
		setAccessors(accessors);
	}

	protected Accessor create(String name, boolean alias, boolean svc, int permit) {
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

		// First untie clone from origin's status set by creation own new one
		clone.rawObjTypes = new HashSet<>();
		// Than populate it with values from origin
		clone.setObjTypes(getObjTypes());

		// First untie clone from origin's status set by creation own new one
		clone.rawStatuses = new HashSet<>();
		// Than populate it with values from origin
		clone.setStatuses(getStatuses());

		// clone.accessors = new HashMap<>();
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

	public Collection<Accessor> getAccessors() {
		// Return a list of contained Accessors ordered by their names
		return accessors.stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
	}

	public void setAccessors(Set<Accessor> newAccessors) {
		if (newAccessors != null) {
			// Remove all previous version of Accessors from ACL
			accessors.clear();
			// Refer new Accessors to ACL
			Set<Accessor> temp = newAccessors.stream().peek(t -> t.setAclId(getId())).collect(Collectors.toSet());
			// Add new Accessors to ACL
			accessors.addAll(temp);
		}
	}

	public void addAccessor(Accessor accessor) {
		if (accessor != null) {
			// Remove previous version of Accessor from ACL
			removeAccessor(accessor.getName());
			// Refer new Accessor to ACL
			accessor.setAclId(getId());
			// Add new Accessor to ACL
			accessors.add(accessor);
		}
	}

	public boolean hasAccessor(String name) {
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

	public Set<String> getStatuses() {
		// Convert Statuses stored in internal data type into String
		return getRawStatuses().stream().map(t -> t.getStatus()).collect(Collectors.toSet());
	}

	public void setStatuses(Set<String> statuses) {
		// Apply new not-null set of Statuses or clear current otherwise
		if (statuses != null) {
			// Convert new Statuses from String to internal data type before applying them
			setRawStatuses(statuses.stream().map(t -> {
				Status status = new Status();
				status.setAclId(this.getId());
				status.setStatus(t);
				return status;
			}).collect(Collectors.toSet()));
		} else {
			rawStatuses.clear();
		}
	}

	public Set<String> getObjTypes() {
		// Convert Obj.Types stored in internal data type into String
		return getRawObjTypes().stream().map(t -> t.getObjType()).collect(Collectors.toSet());
	}

	public void setObjTypes(Set<String> objTypes) {
		// Apply new not-null set of Obj.Types or clear current otherwise
		if (objTypes != null) {
			// Convert new Obj.Types from String to internal data type before applying them
			setRawObjTypes(objTypes.stream().map(t -> {
				ObjType objType = new ObjType();
				objType.setAclId(this.getId());
				objType.setObjType(t);
				return objType;
			}).collect(Collectors.toSet()));
		} else {
			rawObjTypes.clear();
		}
	}

	@Override
	public boolean isNew() {
		return State.NEW.equals(getState());
	}
}
