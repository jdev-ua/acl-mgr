package ua.pp.jdev.permits.data.cassandra;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.pp.jdev.permits.data.Accessor;

@Data
//@AllArgsConstructor
@RequiredArgsConstructor
@UserDefinedType("accessor")
class AccessorUDT {
	@NonNull
	private String name;
	private boolean alias;
	private boolean svc;
	private int permit;

	@Column("org_levels")
	private Set<OrgLevelUDT> orgLevels = new HashSet<>();

	@Column("xpermits")
	private Set<XPermitUDT> xPermits = new HashSet<>();

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

	public Accessor toAccessor() {
		return Accessor.builder()
				.name(getName())
				.permit(getPermit())
				.alias(isAlias())
				.svc(isAlias())
				.orgLevels(getOrgLevels().stream().map(OrgLevelUDT::getOrgLevel).collect(Collectors.toSet()))
				.xPermits(getXPermits().stream().map(XPermitUDT::getXpermit).collect(Collectors.toSet()))
				.build();
	}

	public static AccessorUDT of(Accessor origin) {
		Objects.requireNonNull(origin);

		AccessorUDT result = new AccessorUDT(origin.getName());
		result.setPermit(origin.getPermit());
		result.setAlias(origin.isAlias());
		result.setSvc(origin.isSvc());
		result.setOrgLevels(origin.getOrgLevels().stream().map(t -> new OrgLevelUDT(t)).collect(Collectors.toSet()));
		result.setXPermits(origin.getXPermits().stream().map(t -> new XPermitUDT(t)).collect(Collectors.toSet()));

		return result;
	}
}
