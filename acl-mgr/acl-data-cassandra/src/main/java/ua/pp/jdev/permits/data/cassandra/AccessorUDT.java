package ua.pp.jdev.permits.data.cassandra;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;

@Data
@RequiredArgsConstructor
@UserDefinedType("accessor")
class AccessorUDT {
	@NonNull
	private String name;
	private boolean alias;
	private boolean svc;
	private int permit;

	@Transient
	private State state = State.PURE;

	@Column("org_levels")
	private Set<OrgLevelUDT> orgLevels = new HashSet<>();

	@Column("xpermits")
	private Set<XPermitUDT> xPermits = new HashSet<>();

	public AccessorUDT(State state) {
		setState(state);
	}

	@PersistenceCreator
	public AccessorUDT(String name, int permit, boolean alias, boolean svc, Set<OrgLevelUDT> orgLevels,
			Set<XPermitUDT> xPermits) {
		this(State.PURE);

		setName(name);
		setPermit(permit);
		setAlias(alias);
		setSvc(svc);
		setOrgLevels(orgLevels);
		setXPermits(xPermits);
	}

	public void setOrgLevels(Set<OrgLevelUDT> newOrgLevels) {
		orgLevels.clear();
		if (newOrgLevels != null) {
			orgLevels.addAll(newOrgLevels);
		}
	}

	public void setXPermits(Set<XPermitUDT> newXPermits) {
		xPermits.clear();
		if (newXPermits != null) {
			xPermits.addAll(newXPermits);
		}
	}

	public static Accessor toAccessor(AccessorUDT origin) {
		Accessor result = null;

		if (origin != null) {
			result = new Accessor(origin.getState());
			result.setName(origin.getName());
			result.setPermit(origin.getPermit());
			result.setAlias(origin.isAlias());
			result.setSvc(origin.isSvc());
			result.setState(origin.getState());
			result.setOrgLevels(
					origin.getOrgLevels().stream().map(OrgLevelUDT::getOrgLevel).collect(Collectors.toSet()));
			result.setXPermits(origin.getXPermits().stream().map(XPermitUDT::getXpermit).collect(Collectors.toSet()));
		}

		return result;
	}

	public static AccessorUDT fromAccessor(Accessor origin) {
		AccessorUDT result = null;

		if (origin != null) {
			result = new AccessorUDT(origin.getState());
			result.setName(origin.getName());
			result.setPermit(origin.getPermit());
			result.setAlias(origin.isAlias());
			result.setSvc(origin.isSvc());
			result.setState(origin.getState());
			result.setOrgLevels(
					origin.getOrgLevels().stream().map(t -> new OrgLevelUDT(t)).collect(Collectors.toSet()));
			result.setXPermits(origin.getXPermits().stream().map(t -> new XPermitUDT(t)).collect(Collectors.toSet()));
		}

		return result;
	}
}
