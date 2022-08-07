package ua.pp.jdev.permits.data.mongo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
@Document(collection = "acls")
class MongoACL implements Serializable {
	private static final long serialVersionUID = 4871380951528686011L;

	@Id
	private String id;

	@Transient
	private State state = State.PURE;

	private String name;
	private String description;

	private Set<String> statuses = new HashSet<>();
	private Set<String> objTypes = new HashSet<>();
	private Set<MongoAccessor> accessors = new HashSet<>();

	protected MongoACL(State state) {
		if (State.NEW.equals(state)) {
			setId(IDGenerator.genStringID());
		}

		setState(state);
	}

	@PersistenceCreator
	protected MongoACL(String id, String name, String description, Set<String> statuses, Set<String> objTypes,
			Set<MongoAccessor> accessors) {
		this(State.PURE);
		setName(name);
		setId(id);
		setDescription(description);
		setStatuses(statuses);
		setObjTypes(objTypes);
		setAccessors(accessors);
	}

	public static AccessControlList toAccessControlList(MongoACL origin) {
		AccessControlList result = null;

		if (origin != null) {
			result = new AccessControlList(origin.getState());
			result.setId(origin.getId().toString());
			result.setName(origin.getName());
			result.setDescription(origin.getDescription());
			result.setStatuses(origin.getStatuses());
			result.setObjTypes(origin.getObjTypes());
			result.setAccessors(
					origin.getAccessors().stream().map(MongoAccessor::toAccessor).collect(Collectors.toSet()));
		}

		return result;
	}

	public static MongoACL fromAccessControlList(AccessControlList origin) {
		MongoACL result = null;

		if (origin != null) {
			result = new MongoACL(origin.getState());
			// For newly created ACL skip ID setup to avoid possible errors
			// while parsing a value generated outside Cassandra
			if (!State.NEW.equals(origin.getState())) {
				result.setId(origin.getId());
			}
			result.setName(origin.getName());
			result.setDescription(origin.getDescription());
			result.setStatuses(origin.getStatuses());
			result.setObjTypes(origin.getObjTypes());
			result.setAccessors(
					origin.getAccessors().stream().map(MongoAccessor::fromAccessor).collect(Collectors.toSet()));
		}

		return result;
	}

}
