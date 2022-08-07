package ua.pp.jdev.permits.data.mongo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Transient;

import lombok.Data;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
class MongoAccessor {
	private String id;

	private String name;
	private boolean alias;
	private boolean svc;
	private int permit;

	@Transient
	private State state = State.PURE;

	private Set<String> xPermits = new HashSet<>();
	private Set<String> orgLevels = new HashSet<>();

	public MongoAccessor() {
		this(State.PURE);
	}

	public MongoAccessor(State state) {
		if (State.NEW.equals(state) && getId() == null) {
			setId(IDGenerator.genStringID());
		}

		setState(state);
	}

	public static Accessor toAccessor(MongoAccessor origin) {
		Accessor result = null;

		if (origin != null) {
			result = new Accessor(origin.getState());
			result.setName(origin.getName());
			result.setPermit(origin.getPermit());
			result.setAlias(origin.isAlias());
			result.setSvc(origin.isSvc());
			result.setState(origin.getState());
			result.setOrgLevels(origin.getOrgLevels());
			result.setXPermits(origin.getXPermits());
		}

		return result;
	}

	public static MongoAccessor fromAccessor(Accessor origin) {
		MongoAccessor result = null;

		if (origin != null) {
			result = new MongoAccessor(origin.getState());
			result.setName(origin.getName());
			result.setPermit(origin.getPermit());
			result.setAlias(origin.isAlias());
			result.setSvc(origin.isSvc());
			result.setState(origin.getState());
			result.setOrgLevels(origin.getOrgLevels());
			result.setXPermits(origin.getXPermits());
		}

		return result;
	}
}
