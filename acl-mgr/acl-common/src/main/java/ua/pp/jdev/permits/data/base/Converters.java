package ua.pp.jdev.permits.data.base;

import java.util.Objects;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@NoArgsConstructor(access = AccessLevel.NONE)
final class Converters {
	public static Acl convert(AclRecord aclRec) {
		Objects.requireNonNull(aclRec);
		
		return Acl.builder()
				.id(aclRec.id())
				.name(aclRec.name())
				.state(State.PURE)
				.description(aclRec.description())
				.objTypes(aclRec.objTypes())
				.statuses(aclRec.statuses())
				.accessors(Set.copyOf(aclRec.accessors().stream().map(t -> convert(t)).toList()))
				.build();
	}
	
	public static Accessor convert(AccessorRecord accessorRec) {
		Objects.requireNonNull(accessorRec);
		
		return Accessor.builder()
				.id(accessorRec.id())
				.name(accessorRec.name())
				.state(State.PURE)
				.alias(accessorRec.alias())
				.svc(accessorRec.svc())
				.xPermits(accessorRec.xPermits())
				.orgLevels(accessorRec.orgLevels())
				.build();
	}
	
	public static AclRecord convert(Acl acl) {
		Objects.requireNonNull(acl);
		
		String id = IDGenerator.validateID(acl.getId()) ? acl.getId() : IDGenerator.genStringID();
		
		return new AclRecord(
				id,
				acl.getName(),
				acl.getDescription(),
				acl.getObjTypes(),
				acl.getStatuses(),
				Set.copyOf(acl.getAccessors().stream()
						// Skip Accessors in VOID state before converting
						.filter(accessor -> !State.VOID.equals(accessor.getState()))
						.map(accessor -> convert(accessor))
						.toList()));
	}
	
	public static AccessorRecord convert(Accessor accessor) {
		Objects.requireNonNull(accessor);
		
		String id = IDGenerator.validateID(accessor.getId()) ? accessor.getId() : IDGenerator.genStringID();
		
		return new AccessorRecord(
				id,
				accessor.getName(),
				accessor.isAlias(),
				accessor.isSvc(),
				accessor.getPermit(),
				accessor.getXPermits(),
				accessor.getOrgLevels());
	}
}
