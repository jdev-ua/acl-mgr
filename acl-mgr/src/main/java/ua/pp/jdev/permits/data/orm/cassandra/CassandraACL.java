package ua.pp.jdev.permits.data.orm.cassandra;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import lombok.Data;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.enums.State;

@Data
@Table("acl")
class CassandraACL {
	@PrimaryKey
	private UUID id;

	private String name;
	private String description;

	@Transient
	private State state = State.PURE;

	@Column("statuses")
	private Set<StatusUDT> statuses = new HashSet<>();

	@Column("obj_types")
	private Set<ObjTypeUDT> objTypes = new HashSet<>();

	@Column("accessors")
	private Set<AccessorUDT> accessors = new HashSet<>();

	protected CassandraACL(State state) {
		if (State.NEW.equals(state)) {
			setId(Uuids.timeBased());
		}

		setState(state);
	}

	@PersistenceCreator
	protected CassandraACL(UUID id, String name, String description, Set<StatusUDT> statuses, Set<ObjTypeUDT> objTypes,
			Set<AccessorUDT> accessors) {
		this(State.PURE);
		setName(name);
		setId(id);
		setDescription(description);
		setStatuses(statuses);
		setObjTypes(objTypes);
		setAccessors(accessors);
	}

	public void setAccessors(Set<AccessorUDT> newAccessors) {
		accessors.clear();
		if (newAccessors != null) {
			accessors.addAll(newAccessors);
		}
	}

	public void setStatuses(Set<StatusUDT> newStatuses) {
		statuses.clear();
		if (newStatuses != null) {
			statuses.addAll(newStatuses);
		}
	}

	public void setObjTypes(Set<ObjTypeUDT> newObjTypes) {
		objTypes.clear();
		if (newObjTypes != null) {
			objTypes.addAll(newObjTypes);
		}
	}

	public static AccessControlList toAccessControlList(CassandraACL origin) {
		AccessControlList result = null;

		if (origin != null) {
			result = new AccessControlList(origin.getState());
			result.setId(origin.getId().toString());
			result.setName(origin.getName());
			result.setDescription(origin.getDescription());
			result.setStatuses(origin.getStatuses().stream().map(StatusUDT::getStatus).collect(Collectors.toSet()));
			result.setObjTypes(origin.getObjTypes().stream().map(ObjTypeUDT::getObjType).collect(Collectors.toSet()));
			result.setAccessors(
					origin.getAccessors().stream().map(AccessorUDT::toAccessor).collect(Collectors.toSet()));
		}

		return result;
	}

	public static CassandraACL fromAccessControlList(AccessControlList origin) {
		CassandraACL result = null;

		if (origin != null) {
			result = new CassandraACL(origin.getState());
			// For newly created ACL skip ID setup to avoid possible errors
			// while parsing a value generated outside Cassandra
			if (!State.NEW.equals(origin.getState())) {
				result.setId(UUID.fromString(origin.getId()));
			}
			result.setName(origin.getName());
			result.setDescription(origin.getDescription());
			result.setStatuses(origin.getStatuses().stream().map(t -> new StatusUDT(t)).collect(Collectors.toSet()));
			result.setObjTypes(origin.getObjTypes().stream().map(t -> new ObjTypeUDT(t)).collect(Collectors.toSet()));
			result.setAccessors(
					origin.getAccessors().stream().map(AccessorUDT::fromAccessor).collect(Collectors.toSet()));
		}

		return result;
	}
}
