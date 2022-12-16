package ua.pp.jdev.permits.data;

import static ua.pp.jdev.permits.data.Accessor.DM_OWNER;
import static ua.pp.jdev.permits.data.Accessor.DM_WORLD;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import ua.pp.jdev.permits.enums.Permit;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;;

/**
 * A domain object for Access Control List (ACL)
 * 
 * @author Maksym Shramko
 *
 */
@Data
@Builder(toBuilder = true)
@JsonPOJOBuilder(withPrefix = "")
@JsonDeserialize(builder = Acl.AclBuilder.class)
@EqualsAndHashCode(of = {"name"})
public class Acl {
	private String id;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "{validation.pattern.name}")
	private String name;

	@Length(max = 128, message = "{validation.length.description}")
	private String description;

	@JsonIgnore
	private State state;

	@Singular
	private Set<String> objTypes;
	@Singular
	private Set<String> statuses;
	@Singular
	private Set<Accessor> accessors;
	
	public Acl() {
		id = IDGenerator.EMPTY_ID;
		name = "";
		description = "";
		state = State.NEW;
		objTypes = new HashSet<>();
		statuses = new HashSet<>();
		accessors = new TreeSet<>();
		
		addAccessor(Accessor.builder().name(DM_OWNER).permit(Permit.DELETE.getValue()).build());
		addAccessor(Accessor.builder().name(DM_WORLD).permit(Permit.READ.getValue()).build());
	}

	private Acl(String id, String name, String description, State state, Set<String> objTypes, Set<String> statuses, Set<Accessor> accessors) {
		this();
		if (name != null) {
			setName(name);
		}
		if (state != null) {
			setState(state);
		}
		if(IDGenerator.validateID(id)) {
			setId(id);
		}
		setObjTypes(objTypes);
		setStatuses(statuses);
		setAccessors(accessors);
	}

	/**
	 * Adds the specified {@code Accessor} to this ACL
	 * 
	 * @param accessor an {@code Accessor}
	 */
	public void addAccessor(Accessor accessor) {
		Objects.requireNonNull(accessor);
		
		// Remove previous version
		removeAccessor(accessor.getName());
		
		// Add new Accessor to ACL
		accessors.add(accessor);
	}

	/**
	 * Returns {@code true} if this ACL contains an {@code Accessor} with given name
	 * 
	 * @param accessorName a name of {@code Accessor}
	 * @return {@code true} if this ACL contains an {@code Accessor} with given name
	 */
	public boolean hasAccessor(String accessorName) {
		return accessors.stream().anyMatch(t -> t.getName().equalsIgnoreCase(accessorName));
	}

	/**
	 * Returns an {@code Optional} with {@code Accessor} if this ACL contains
	 * {@code Accessor} with given name or empty {@code Optional} otherwise
	 * 
	 * @param accessorName a name of {@code Accessor}
	 * @return an {@code Optional} with {@code Accessor} or empty {@code Optional}
	 */
	public Optional<Accessor> getAccessor(String accessorName) {
		return accessors.stream().filter(t -> t.getName().equalsIgnoreCase(accessorName)).findAny();
	}

	/**
	 * Removes an {@code Accessor} with given name from this ACL
	 * 
	 * @param accessorName a name of {@code Accessor}
	 */
	public void removeAccessor(String accessorName) {
		if (!DM_WORLD.equalsIgnoreCase(accessorName) && !DM_OWNER.equalsIgnoreCase(accessorName)) {
			accessors.removeIf(t -> t.getName().equalsIgnoreCase(accessorName));
		}
	}

	/**
	 * Adds all of the {@code Accessor} in the specified set to this ACL previously
	 * clearing all contained ones except required ("dm_world" or "dm_owner").
	 * 
	 * @param newAccessors a {@code Set} of {@code Accessor}
	 */
	public void setAccessors(Set<Accessor> newAccessors) {
		Objects.requireNonNull(newAccessors);
		
		Accessor owner = null;
		// Backup actual 'dm_owner' if set of new Accessors doesn't contain such one
		if (newAccessors.isEmpty() || !newAccessors.stream().anyMatch(t -> DM_OWNER.equalsIgnoreCase(t.getName()))) {
			Optional<Accessor> optOwner = getAccessor(DM_OWNER);
			if(optOwner.isPresent()) owner = optOwner.get();
		}
		
		Accessor world = null;
		// Backup actual 'dm_world' if set of new Accessors doesn't contain such one
		if (newAccessors.isEmpty() || !newAccessors.stream().anyMatch(t -> DM_WORLD.equalsIgnoreCase(t.getName()))) {
			Optional<Accessor> optWorld = getAccessor(DM_WORLD);
			if(optWorld.isPresent()) world = optWorld.get();
		}
		
		// Remove all previous version of Accessors from ACL
		accessors.clear();
		
		// Add new Accessors to ACL
		newAccessors.forEach(this::addAccessor);
		
		// Restore 'dm_owner' if it is necessary
		if(owner != null) {
			addAccessor(owner);
		}
		
		// Restore 'dm_world' if it is necessary
		if(world != null) {
			addAccessor(world);
		}
	}
	
	public void setStatuses(Set<String> statuses) {
		this.statuses.clear();
		
		if(statuses != null) {
			this.statuses.addAll(statuses);
		}
	}
	
	public void setObjTypes(Set<String> objTypes) {
		this.objTypes.clear();
		
		if(objTypes != null) {
			this.objTypes.addAll(objTypes);
		}
	}
}
