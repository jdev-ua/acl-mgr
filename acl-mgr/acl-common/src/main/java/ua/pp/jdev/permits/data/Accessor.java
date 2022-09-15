package ua.pp.jdev.permits.data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

/**
 * A domain object for Accessor of Access Control List
 * 
 * @author Maksym Shramko
 *
 */
@Data
public class Accessor {
	private String id;

	@NotBlank(message = "{validation.notblank.name}")
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

		setName("");
		setState(state);
	}
	
	private Accessor(Accessor origin, boolean clone) {
		this(clone ? origin.getState() : State.NEW);
		if(clone) {
			setId(origin.getId());
		}
		setName(origin.getName());
		setAlias(origin.isAlias());
		setSvc(origin.isSvc());
		setPermit(origin.getPermit());
		setOrgLevels(origin.getOrgLevels());
		setXPermits(origin.getXPermits());
	}
	
	/**
	 * Creates a new copy (has unique ID and {@code NEW} state,
	 * all other fields are the same to origin) of the given {@code Accessor}.
	 * The given {@code Accessor} must not be {@code null}
	 * 
	 * @param origin a {@code Accessor} to be copied, must be non-null
	 * @return a new copy of the given {@code Accessor}
	 * @throws NullPointerException if origin is {@code null}
	 */
	public static Accessor softCopy(Accessor origin) {
		Objects.requireNonNull(origin);
		return new Accessor(origin, false);
	}
	
	/**
	 * Returns a full clone (all fields, including ID and state,
	 * are the same to origin) of the given {@code Accessor}. The given
	 * {@code Accessor} must not be {@code null}
	 * 
	 * @param origin a {@code AccessControlList} to be cloned, must be non-null
	 * @return a full clone of the given {@code Accessor}
	 * @throws NullPointerException if origin is {@code null}
	 */
	public static Accessor deepCopy(Accessor origin) {
		Objects.requireNonNull(origin);
		return new Accessor(origin, true);
	}
}
