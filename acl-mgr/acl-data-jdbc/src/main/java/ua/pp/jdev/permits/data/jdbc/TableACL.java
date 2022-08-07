package ua.pp.jdev.permits.data.jdbc;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
@Table("ACL")
class TableACL implements Serializable, Persistable<Long> {
	private static final long serialVersionUID = -6820394109565967621L;

	@Id
	private Long id;
	private String name;
	private String description;

	@Transient
	private State state = State.PURE;

	@MappedCollection(idColumn = "ACL_ID")
	private Set<TableStatus> statuses = new HashSet<>();

	@MappedCollection(idColumn = "ACL_ID")
	private Set<TableObjType> objTypes = new HashSet<>();

	@MappedCollection(idColumn = "ACL_ID")
	private Set<TableAccessor> accessors = new HashSet<>();

	protected TableACL(State state) {
		if (State.NEW.equals(state)) {
			setId(IDGenerator.genLongID());
		}

		setState(state);
	}

	@PersistenceCreator
	protected TableACL(Long id, String name, String description, Set<TableStatus> statuses, Set<TableObjType> objTypes,
			Set<TableAccessor> accessors) {
		this(State.PURE);
		setName(name);
		setId(id);
		setDescription(description);
		setStatuses(statuses);
		setObjTypes(objTypes);
		setAccessors(accessors);
	}

	@Override
	public boolean isNew() {
		return State.NEW.equals(getState());
	}

	public static AccessControlList toAccessControlList(TableACL origin) {
		AccessControlList result = null;

		if (origin != null) {
			result = new AccessControlList(origin.getState());
			result.setId(String.valueOf(origin.getId()));
			result.setName(origin.getName());
			result.setDescription(origin.getDescription());
			result.setObjTypes(origin.getObjTypes().stream().map(TableObjType::getObjType).collect(Collectors.toSet()));
			result.setStatuses(origin.getStatuses().stream().map(TableStatus::getStatus).collect(Collectors.toSet()));
			result.setAccessors(
					origin.getAccessors().stream().map(TableAccessor::toAccessor).collect(Collectors.toSet()));
		}

		return result;
	}

	public static TableACL fromAccessControlList(AccessControlList origin) {
		TableACL result = null;

		if (origin != null) {
			Long id = Long.parseLong(origin.getId());

			result = new TableACL(origin.getState());
			result.setId(id);
			result.setName(origin.getName());
			result.setDescription(origin.getDescription());
			result.setObjTypes(
					origin.getObjTypes().stream().map(t -> new TableObjType(null, id, t)).collect(Collectors.toSet()));
			result.setStatuses(
					origin.getStatuses().stream().map(t -> new TableStatus(null, id, t)).collect(Collectors.toSet()));
			result.setAccessors(origin.getAccessors().stream().map(t -> {
				TableAccessor temp = TableAccessor.fromAccessor(t);
				temp.setAclId(id);
				return temp;
			}).collect(Collectors.toSet()));
		}

		return result;
	}
}
