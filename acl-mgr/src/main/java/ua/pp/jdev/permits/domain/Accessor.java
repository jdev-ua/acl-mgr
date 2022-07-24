package ua.pp.jdev.permits.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.Setter;
import lombok.AccessLevel;

@Data
public class Accessor implements Cloneable {
	private Long id = Long.valueOf(0);

	@NotBlank(message = "{validation.notblank.name}")
	private String name;
	private boolean alias;
	private boolean svc;
	@Min(1)
	@Max(7)
	private int permit;
	
	@Setter(AccessLevel.PROTECTED)
	private String state = "new";

	private Set<String> xPermits = new HashSet<>();
	private Set<String> orgLevels = new HashSet<>();

	@Override
	public Accessor clone() throws CloneNotSupportedException {
		Accessor clone = (Accessor) super.clone();
		clone.xPermits = new HashSet<>(getXPermits());
		clone.orgLevels = new HashSet<>(getOrgLevels());
		return clone;
	}
	
	public void markDeleted() {
		setState("deleted");
	}
	
	public void markDirty() {
		setState("dirty");
	}
	
	public void markOk() {
		setState("ok");
	}
}
