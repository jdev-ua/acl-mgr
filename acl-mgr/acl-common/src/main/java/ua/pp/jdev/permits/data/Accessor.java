package ua.pp.jdev.permits.data;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
public class Accessor implements Cloneable {
	private String id;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "{validation.pattern.name}")
	private String name;
	private boolean alias;
	private boolean svc;
	@Min(1)
	@Max(7)
	private int permit;

	private State state = State.PURE;
	
	private Set<String> xPermits = new HashSet<>();
	private Set<String> orgLevels = new HashSet<>();

	public Accessor() {
		this(State.PURE);
	}

	public Accessor(State state) {
		if (State.NEW.equals(state) && getId() == null) {
			setId(IDGenerator.genStringID());
		}

		setState(state);
	}
	
	@Override
	public Accessor clone() throws CloneNotSupportedException {
		Accessor clone = (Accessor) super.clone();
		clone.xPermits = new HashSet<>(getXPermits());
		clone.orgLevels = new HashSet<>(getOrgLevels());
		return clone;
	}
}
