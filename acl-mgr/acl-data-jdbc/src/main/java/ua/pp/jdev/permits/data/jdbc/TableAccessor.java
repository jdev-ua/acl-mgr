package ua.pp.jdev.permits.data.jdbc;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
@Table("ACCESSOR")
@NoArgsConstructor
@EqualsAndHashCode(of = {"name"})
class TableAccessor implements Serializable {
	private static final long serialVersionUID = -2252261108300139174L;

	@Id
	private Long id;

	private Long aclId;
	private String name;
	private boolean alias;
	private boolean svc;
	private int permit;

	@Transient
	private Set<TableOrgLevel> orgLevels = new HashSet<>();

	@Transient
	private Set<TableXPermit> xPermits = new HashSet<>();

	@PersistenceCreator
	public TableAccessor(Long id, Long aclId, String name, int permit, boolean alias, boolean svc) {
		setName(name);
		setId(id);
		setAclId(aclId);
		setPermit(permit);
		setAlias(alias);
		setSvc(svc);
	}

	public Accessor toAccessor() {
		return Accessor.builder()
				.id(String.valueOf(getId()))
				.name(getName())
				.state(State.PURE)
				.permit(getPermit())
				.alias(isAlias())
				.svc(isSvc())
				.orgLevels(getOrgLevels().stream().map(TableOrgLevel::getOrgLevel).collect(Collectors.toSet()))
				.xPermits(getXPermits().stream().map(TableXPermit::getXPermit).collect(Collectors.toSet()))
				.build();
	}

	public static TableAccessor of(Accessor origin) {
		Objects.requireNonNull(origin);
		
		Long id = IDGenerator.validateID(origin.getId()) ? Long.parseLong(origin.getId()) : null;

		TableAccessor result = new TableAccessor();
		result.setName(origin.getName());
		result.setPermit(origin.getPermit());
		result.setAlias(origin.isAlias());
		result.setSvc(origin.isSvc());
		result.setId(id);
		result.setOrgLevels(origin.getOrgLevels().stream().map(t -> new TableOrgLevel(null, id, t)).collect(Collectors.toSet()));
		result.setXPermits(origin.getXPermits().stream().map(t -> new TableXPermit(null, id, t)).collect(Collectors.toSet()));

		return result;
	}
}
