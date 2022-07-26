package ua.pp.jdev.permits.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import ua.pp.jdev.permits.enums.State;

@Data
public class Accessor implements Cloneable {
	private Long id = Long.valueOf(0);

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "{validation.pattern.name}")
	private String name;
	private boolean alias;
	private boolean svc;
	@Min(1)
	@Max(7)
	private int permit;

	private State state;

	private Set<String> xPermits = new HashSet<>();
	private Set<String> orgLevels = new HashSet<>();

	public Accessor() {
		this(State.PURE);
	}

	public Accessor(State state) {
		this.state = state;
	}

	@Override
	public Accessor clone() throws CloneNotSupportedException {
		Accessor clone = (Accessor) super.clone();
		clone.xPermits = new HashSet<>(getXPermits());
		clone.orgLevels = new HashSet<>(getOrgLevels());
		return clone;
	}
}
