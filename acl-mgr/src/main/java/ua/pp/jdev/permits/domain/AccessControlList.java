package ua.pp.jdev.permits.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class AccessControlList {
	private String id;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	private String name = "";

	@Length(max = 128, message = "{validation.length.description}")
	private String description = "";

	private Set<String> objTypes = new HashSet<>();

	private Set<String> statuses = new HashSet<>();

	private List<Accessor> accessors = new ArrayList<>();

	public AccessControlList() {
		// Add dm_owner with default setting
		accessors.add(create("dm_owner", false, false, 7));
		// Add dm_world with default setting
		accessors.add(create("dm_world", false, false, 3));
	}

	private Accessor create(String name, boolean alias, boolean svc, int permit) {
		Accessor result = new Accessor();
		result.setName(name);
		result.setAlias(alias);
		result.setSvc(svc);
		result.setPermit(permit);
		return result;
	}

	public void setAccessors(List<Accessor> newAccessors) {
		if(newAccessors == null) {
			// Clear actual list
			accessors.clear();
			// Add dm_owner with default setting
			accessors.add(create("dm_owner", false, false, 7));
			// Add dm_world with default setting
			accessors.add(create("dm_world", false, false, 3));
		} else {
			boolean containsOwner = false;
			boolean containsWorld = false;
			
			// Check whether list of new values contains dm_world and dm_owner
			for(Accessor accessor: newAccessors) {
				if("dm_owner".equalsIgnoreCase(accessor.getName())) {
					containsOwner = true;
				}
				if("dm_world".equalsIgnoreCase(accessor.getName())) {
					containsWorld = true;
				}
			};
			
			// Clear actual list
			accessors.clear();
			// Extend list with new accessors
			accessors.addAll(newAccessors);
			// Add dm_owner with default setting if it is necessary
			if(!containsOwner) {
				accessors.add(create("dm_owner", false, false, 7));
			}
			// Add dm_world with default setting if it is necessary
			if(!containsWorld) {
				accessors.add(create("dm_world", false, false, 3));
			}
		}
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
