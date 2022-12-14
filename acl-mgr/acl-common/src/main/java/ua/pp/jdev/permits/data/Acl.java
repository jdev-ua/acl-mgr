package ua.pp.jdev.permits.data;

import static ua.pp.jdev.permits.data.Accessor.DM_OWNER;
import static ua.pp.jdev.permits.data.Accessor.DM_WORLD;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
@JsonDeserialize(builder = Acl.AclBuilder.class)
@EqualsAndHashCode(of = {"name"})
public class Acl {
	private String id;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "{validation.pattern.name}")
	private String name = "";

	@Length(max = 128, message = "{validation.length.description}")
	private String description = "";

	@JsonIgnore
	private State state;

	private Set<String> objTypes = new HashSet<>();
	private Set<String> statuses = new HashSet<>();
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	//private Map<String, Accessor> accessors = new HashMap<>();
	private Map<String, Accessor> accessors = new TreeMap<>();

	protected Acl(State state) {
		setState(state);
		
		/*
		Accessor.AccessorBuilder accessorBuilder = Accessor.builder();
		
		// Add dm_owner with default setting
		addAccessor(accessorBuilder.name(DM_OWNER).permit(Permit.DELETE.getValue()).build());
		// Add dm_world with default setting
		addAccessor(accessorBuilder.name(DM_WORLD).permit(Permit.READ.getValue()).build());
		*/
	}

	/**
	 * Adds the specified {@code Accessor} to this ACL
	 * 
	 * @param accessor an {@code Accessor}
	 */
	public void addAccessor(Accessor accessor) {
		Objects.requireNonNull(accessor);
		
		//if (accessor != null) {
		/*if (hasAccessor(accessor.getName())) {
			// Remove previous version of Accessor from ACL
			removeAccessor(accessor.getName());

			//accessor.setState(State.DIRTY);
		} else {

		}	*/		
			
			
		// Add new Accessor to ACL
		accessors.put(accessor.getName(), accessor);
		//}
	}

	/**
	 * Returns {@code true} if this ACL contains an {@code Accessor} with given name
	 * 
	 * @param accessorName a name of {@code Accessor}
	 * @return {@code true} if this ACL contains an {@code Accessor} with given name
	 */
	public boolean hasAccessor(String accessorName) {
		return accessors.containsKey(accessorName);
		// return getAccessor(accessorName).isPresent();
	}

	/**
	 * Returns an {@code Optional} with {@code Accessor} if this ACL contains
	 * {@code Accessor} with given name or empty {@code Optional} otherwise
	 * 
	 * @param accessorName a name of {@code Accessor}
	 * @return an {@code Optional} with {@code Accessor} or empty {@code Optional}
	 */
	public Optional<Accessor> getAccessor(String accessorName) {
		// return accessors.stream().filter(t ->
		// t.getName().equalsIgnoreCase(accessorName)).findFirst();
		return hasAccessor(accessorName) ? Optional.of(accessors.get(accessorName)) : Optional.empty();
	}

	/**
	 * Removes an {@code Accessor} with given name from this ACL
	 * 
	 * @param accessorName a name of {@code Accessor}
	 */
	public void removeAccessor(String accessorName) {
		if (!DM_WORLD.equalsIgnoreCase(accessorName) && !DM_OWNER.equalsIgnoreCase(accessorName)) {
			/*Optional<Accessor> optional = getAccessor(accessorName);
			if (optional.isPresent()) {
				accessors.remove(optional.get().getName());
			}*/
			accessors.remove(accessorName);
		}
	}

	/**
	 * Returns a {@code Collection} of contained {@code Accessor} ordered by name
	 * 
	 * @return a {@code Collection} of contained {@code Accessor}
	 */
	public Collection<Accessor> getAccessors() {
		//return accessors.values().stream().sorted(Comparator.comparing(Accessor::getName)).collect(Collectors.toSet());
		return accessors.values();
	}

	/**
	 * Adds all of the {@code Accessor} in the specified set to this ACL previously
	 * clearing all contained ones except required ("dm_world" or "dm_owner").
	 * 
	 * @param newAccessors a {@code Set} of {@code Accessor}
	 */
	public void setAccessors(Set<Accessor> newAccessors) {
		Objects.requireNonNull(newAccessors);
		
		//System.out.println("newAccessors[B-" + newAccessors.size() + "]: " + newAccessors);

		// if (newAccessors != null) {
		if (newAccessors.isEmpty() || newAccessors.stream().filter(t1 -> /*{
			System.out.println("t1: " + t1);
			return */DM_OWNER.equalsIgnoreCase(t1.getName())/*;}*/).findAny().isEmpty()) {
			/*Accessor owner = accessors.get(DM_OWNER);
			System.out.println("owner: " + owner);
			if(owner != null)
			newAccessors.add(owner);*/
			
			if(hasAccessor(DM_OWNER)) newAccessors.add(accessors.get(DM_OWNER));
		}
		if (newAccessors.isEmpty() || newAccessors.stream().filter(t2 -> /*{
			System.out.println("t2: " + t2);
			return */DM_WORLD.equalsIgnoreCase(t2.getName())/*;}*/).findAny().isEmpty()) {
			/*Accessor world = accessors.get(DM_WORLD);
			System.out.println("world: " + world);
			if(world != null)
			newAccessors.add(world);*/
			if(hasAccessor(DM_WORLD)) newAccessors.add(accessors.get(DM_WORLD));
		}
		//System.out.println("newAccessors[A-" + newAccessors.size() + "]: " + newAccessors);

		// Remove all previous version of Accessors from ACL
		accessors.clear();
		// Add new Accessors to ACL
		newAccessors.forEach(this::addAccessor);
		// }
	}
	
	public static Acl of(Acl origin) {
		Objects.requireNonNull(origin);
		
		return builder()
				.id(origin.getId())
				.name(origin.getName())
				.description(origin.getDescription())
				.statuses(origin.getStatuses())
				.objTypes(origin.getObjTypes())
				.accessors(Set.copyOf(origin.getAccessors()))
				.build();
	}

	/**
	 * Provides a builder object for {@code AccessControlList} construction
	 * @return a builder object
	 */
	public static AclBuilder builder() {
		return new AclBuilder();
	}

	@Data
	@Getter(AccessLevel.NONE)
	@Accessors(fluent = true)
	@JsonPOJOBuilder(withPrefix = "")
	public static class AclBuilder {
		private String id;
		private State state;
		private String name;
		private String description;
		private Set<String> objTypes;
		private Set<String> statuses;
		private Set<Accessor> accessors = new HashSet<>();
		
		public void reset() {
			id = null;
			name = null;
			description = null;
			objTypes = null;
			statuses = null;
			accessors.clear();
		}
		
		public AclBuilder accessors(Set<Accessor> accessors) {
			this.accessors.addAll(accessors);
			return this;
		}
		
		public AclBuilder accessor(Accessor accessor) {
			accessors.add(accessor);
			return this;
		}

		public Acl build() {
			Acl acl;

			if (!IDGenerator.validateID2(id)) {
				acl = new Acl(State.NEW);
				acl.setId(IDGenerator.EMPTY_ID);
			} else {
				acl = new Acl(State.PURE);
				acl.setId(id);
			}

			if (name != null) {
				acl.setName(name);
			}

			if (description != null) {
				acl.setDescription(description);
			}

			if (objTypes != null) {
				acl.setObjTypes(objTypes);
			}

			if (statuses != null) {
				acl.setStatuses(statuses);
			}

			if (accessors != null) {
				acl.setAccessors(accessors);
			}
			
			if(!acl.hasAccessor(DM_OWNER)) {
				acl.addAccessor(Accessor.builder().name(DM_OWNER).permit(Permit.DELETE.getValue()).build());
			}
			
			if(!acl.hasAccessor(DM_WORLD)) {
				acl.addAccessor(Accessor.builder().name(DM_WORLD).permit(Permit.READ.getValue()).build());
			}

			return acl;
		}
	}
}
