package ua.pp.jdev.permits.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.Setter;
import lombok.AccessLevel;

@Data
public class AccessControlList {
	private String id;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	private String name = "";

	@Length(max = 128, message = "{validation.length.description}")
	private String description = "";

	@Setter(AccessLevel.NONE)
	private Set<String> objTypes = new HashSet<>();

	@Setter(AccessLevel.NONE)
	private Set<String> statuses = new HashSet<>();

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
