package ua.pp.jdev.permits.data.cassandra;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
@Table("acl")
@NoArgsConstructor
class CassandraACL {
	@PrimaryKey
	private UUID id;

	private String name;
	private String description;

	@Column("statuses")
	private Set<StatusUDT> statuses = new HashSet<>();

	@Column("obj_types")
	private Set<ObjTypeUDT> objTypes = new HashSet<>();

	@Column("accessors")
	private Set<AccessorUDT> accessors = new HashSet<>();

	@PersistenceCreator
	protected CassandraACL(UUID id, String name, String description, Set<StatusUDT> statuses, Set<ObjTypeUDT> objTypes,
			Set<AccessorUDT> accessors) {
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

	public Acl toAcl() {
		return Acl.builder()
				.id(getId().toString())
				.name(getName())
				.description(getDescription())
				.statuses(getStatuses().stream().map(StatusUDT::getStatus).collect(Collectors.toSet()))
				.objTypes(getObjTypes().stream().map(ObjTypeUDT::getObjType).collect(Collectors.toSet()))
				.accessors(getAccessors().stream().map(AccessorUDT::toAccessor).collect(Collectors.toSet()))
				.build();
	}

	public static CassandraACL of(Acl origin) {
		Objects.requireNonNull(origin);

		CassandraACL result = new CassandraACL();
		// For newly created ACL skip ID setup to avoid possible errors
		// while parsing a value generated outside Cassandra
		if (IDGenerator.validateID(origin.getId())) {
			result.setId(UUID.fromString(origin.getId()));
		}
		result.setName(origin.getName());
		result.setDescription(origin.getDescription());
		result.setStatuses(origin.getStatuses().stream().map(t -> new StatusUDT(t)).collect(Collectors.toSet()));
		result.setObjTypes(origin.getObjTypes().stream().map(t -> new ObjTypeUDT(t)).collect(Collectors.toSet()));
		result.setAccessors(origin.getAccessors().stream().map(AccessorUDT::of).collect(Collectors.toSet()));

		return result;
	}
}
