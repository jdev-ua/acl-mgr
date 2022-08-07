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
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;

@Data
@Table("ACCESSOR")
class TableAccessor implements Serializable, Persistable<Long> {
	private static final long serialVersionUID = -2252261108300139174L;

	@Id
	private Long id;

	private Long aclId;
	private String name;
	private boolean alias;
	private boolean svc;
	private int permit;

	@Transient
	private State state = State.PURE;

	@MappedCollection(idColumn = "ACCESSOR_ID")
	private Set<TableOrgLevel> orgLevels = new HashSet<>();

	@MappedCollection(idColumn = "ACCESSOR_ID")
	private Set<TableXPermit> xPermits = new HashSet<>();

	public TableAccessor(State state) {
		setState(state);
	}

	@PersistenceCreator
	public TableAccessor(Long id, Long aclId, String name, int permit, boolean alias, boolean svc,
			Set<TableOrgLevel> orgLevels, Set<TableXPermit> xPermits) {
		this(State.PURE);

		setName(name);
		setId(id);
		setAclId(aclId);
		setPermit(permit);
		setAlias(alias);
		setSvc(svc);
		setOrgLevels(orgLevels);
		setXPermits(xPermits);
	}

	@Override
	public boolean isNew() {
		return State.NEW.equals(getState());
	}

	public static Accessor toAccessor(TableAccessor origin) {
		Accessor result = null;

		if (origin != null) {
			result = new Accessor(origin.getState());
			result.setName(origin.getName());
			result.setPermit(origin.getPermit());
			result.setAlias(origin.isAlias());
			result.setSvc(origin.isSvc());
			result.setId(String.valueOf(origin.getId()));
			result.setState(origin.getState());
			result.setOrgLevels(
					origin.getOrgLevels().stream().map(TableOrgLevel::getOrgLevel).collect(Collectors.toSet()));
			result.setXPermits(origin.getXPermits().stream().map(TableXPermit::getXPermit).collect(Collectors.toSet()));
		}

		return result;
	}

	public static TableAccessor fromAccessor(Accessor origin) {
		TableAccessor result = null;

		if (origin != null) {
			Long id = Long.parseLong(origin.getId());

			result = new TableAccessor(origin.getState());
			result.setName(origin.getName());
			result.setPermit(origin.getPermit());
			result.setAlias(origin.isAlias());
			result.setSvc(origin.isSvc());
			result.setId(id);
			result.setState(origin.getState());
			result.setOrgLevels(origin.getOrgLevels().stream().map(t -> new TableOrgLevel(null, id, t))
					.collect(Collectors.toSet()));
			result.setXPermits(
					origin.getXPermits().stream().map(t -> new TableXPermit(null, id, t)).collect(Collectors.toSet()));
		}

		return result;
	}
}
