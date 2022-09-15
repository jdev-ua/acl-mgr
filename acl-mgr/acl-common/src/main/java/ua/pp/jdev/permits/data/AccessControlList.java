package ua.pp.jdev.permits.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

/**
 * A domain object for Access Control List (ACL)
 * 
 * @author Maksym Shramko
 *
 */
@Data
public class AccessControlList {
	private final static String DM_OWNER = "dm_owner";
	private final static String DM_WORLD = "dm_world";

	private String id;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "{validation.pattern.name}")
	private String name = "";

	@Length(max = 128, message = "{validation.length.description}")
	private String description = "";

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
		addAccessor(create(DM_OWNER, false, false, 7, state));
		// Add dm_world with default setting
		addAccessor(create(DM_WORLD, false, false, 3, state));

		setState(state);
	}

	private AccessControlList(AccessControlList origin, boolean clone) {
		this(clone ? origin.getState() : State.NEW);
		if (clone) {
			setId(origin.getId());
		}
		setName(origin.getName());
		setDescription(origin.getDescription());
		setObjTypes(origin.getObjTypes());
		setStatuses(origin.getStatuses());
		setAccessors(origin.getAccessors().stream().map(t -> Accessor.deepCopy(t)).collect(Collectors.toSet()));
	}

	private Accessor create(String accessorName, boolean alias, boolean svc, int permit, State state) {
		// Accessor result = new Accessor(State.NEW);
		Accessor result = new Accessor(state);
		result.setName(accessorName);
		result.setAlias(alias);
		result.setSvc(svc);
		result.setPermit(permit);
		return result;
	}

	/**
	 * Adds the specified {@code Accessor} to this ACL
	 * 
	 * @param accessor an {@code Accessor}
	 */
	public void addAccessor(Accessor accessor) {
		if (accessor != null) {
			// Remove previous version of Accessor from ACL
			removeAccessor(accessor.getName());
			// Add new Accessor to ACL
			accessors.add(accessor);
		}
	}

	/**
	 * Returns {@code true} if this ACL contains an {@code Accessor} with given name
	 * 
	 * @param accessorName a name of {@code Accessor}
	 * @return {@code true} if this ACL contains an {@code Accessor} with given name
	 */
	public boolean hasAccessor(String accessorName) {
		return getAccessor(accessorName).isPresent();
	}

	/**
	 * Returns an {@code Optional} with {@code Accessor} if this ACL contains
	 * {@code Accessor} with given name or empty {@code Optional} otherwise
	 * 
	 * @param accessorName a name of {@code Accessor}
	 * @return an {@code Optional} with {@code Accessor} or empty {@code Optional}
	 */
	public Optional<Accessor> getAccessor(String accessorName) {
		return accessors.stream().filter(t -> t.getName().equalsIgnoreCase(accessorName)).findFirst();
	}

	/**
	 * Removes an {@code Accessor} with given name from this ACL
	 * 
	 * @param accessorName a name of {@code Accessor}
	 */
	public void removeAccessor(String accessorName) {
		if (!DM_WORLD.equalsIgnoreCase(accessorName) && !DM_OWNER.equalsIgnoreCase(accessorName)) {
			Optional<Accessor> optional = getAccessor(accessorName);
			if (optional.isPresent()) {
				accessors.remove(optional.get());
			}
		}
	}

	/**
	 * Returns a {@code Collection} of contained {@code Accessor} ordered by name
	 * 
	 * @return a {@code Collection} of contained {@code Accessor}
	 */
	public Collection<Accessor> getAccessors() {
		return accessors.stream().sorted(Comparator.comparing(Accessor::getName)).collect(Collectors.toList());
	}

	/**
	 * Adds all of the {@code Accessor} in the specified set to this ACL previously
	 * clearing all contained ones except required ("dm_world" or "dm_owner").
	 * 
	 * @param newAccessors a {@code Set} of {@code Accessor}
	 */
	public void setAccessors(Set<Accessor> newAccessors) {
		if (newAccessors != null) {
			// Remove all previous version of Accessors from ACL
			accessors.clear();
			// Add new Accessors to ACL
			accessors.addAll(newAccessors);
		}
	}

	/**
	 * Creates a new copy (has unique ID and {@code NEW} state, all other fields are
	 * the same to origin) of the given {@code AccessControlList}. The given
	 * {@code AccessControlList} must not be {@code null}
	 * 
	 * @param origin a {@code AccessControlList} to be copied, must be non-null
	 * @return a new copy of the given {@code AccessControlList}
	 * @throws NullPointerException if origin is {@code null}
	 */
	public static AccessControlList softCopy(AccessControlList origin) {
		Objects.requireNonNull(origin);
		return new AccessControlList(origin, false);
	}

	/**
	 * Returns a full clone (all fields, including ID and state, are the same to
	 * origin) of the given {@code AccessControlList}. The given
	 * {@code AccessControlList} must not be {@code null}
	 * 
	 * @param origin a {@code AccessControlList} to be cloned, must be non-null
	 * @return a full clone of the given {@code AccessControlList}
	 * @throws NullPointerException if origin is {@code null}
	 */
	public static AccessControlList deepCopy(AccessControlList origin) {
		Objects.requireNonNull(origin);
		return new AccessControlList(origin, true);
	}
}
