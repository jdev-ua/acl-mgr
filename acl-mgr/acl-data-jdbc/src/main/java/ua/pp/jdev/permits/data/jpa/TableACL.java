package ua.pp.jdev.permits.data.jpa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
@Table("ACL")
@NoArgsConstructor
class TableACL implements Serializable {
	private static final long serialVersionUID = -6820394109565967621L;

	@Id
	private Long id;
	private String name;
	private String description;

	@Transient
	private Set<TableStatus> statuses = new HashSet<>();

	@Transient
	private Set<TableObjType> objTypes = new HashSet<>();

	@Transient
	private Set<TableAccessor> accessors = new HashSet<>();

	@PersistenceCreator
	protected TableACL(Long id, String name, String description) {
		setName(name);
		setId(id);
		setDescription(description);
	}

	public Acl toAcl() {
		return Acl.builder()
				.id(getId().toString())
				.name(getName())
				.description(getDescription())
				.state(State.PURE)
				.statuses(getStatuses().stream().map(TableStatus::getStatus).collect(Collectors.toSet()))
				.objTypes(getObjTypes().stream().map(TableObjType::getObjType).collect(Collectors.toSet()))
				.accessors(getAccessors().stream().map(TableAccessor::toAccessor).collect(Collectors.toSet()))
				.build();
	}

	public static TableACL of(Acl origin) {
		Objects.requireNonNull(origin);

		Long id = IDGenerator.validateID(origin.getId()) ? Long.parseLong(origin.getId()) : null;

		TableACL result = new TableACL(id, origin.getName(), origin.getDescription());
		result.setObjTypes(origin.getObjTypes().stream().map(t -> new TableObjType(null, id, t)).collect(Collectors.toSet()));
		result.setStatuses(origin.getStatuses().stream().map(t -> new TableStatus(null, id, t)).collect(Collectors.toSet()));
		result.setAccessors(origin.getAccessors().stream().filter(t -> !State.VOID.equals(t.getState())).map(t -> {
			TableAccessor temp = TableAccessor.of(t);
			temp.setAclId(id);
			return temp;
		}).collect(Collectors.toSet()));

		return result;
	}
}
