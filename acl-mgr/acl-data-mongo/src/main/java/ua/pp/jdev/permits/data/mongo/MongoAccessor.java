package ua.pp.jdev.permits.data.mongo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Data;
import ua.pp.jdev.permits.data.Accessor;

@Data
class MongoAccessor {
	private String id;

	private String name;
	private boolean alias;
	private boolean svc;
	private int permit;

	private Set<String> xPermits = new HashSet<>();
	private Set<String> orgLevels = new HashSet<>();

	public Accessor toAccessor() {
		return Accessor.builder()
				.name(getName())
				.permit(getPermit())
				.alias(isAlias())
				.svc(isAlias())
				.orgLevels(getOrgLevels())
				.xPermits(getXPermits())
				.build();
	}

	public static MongoAccessor of(Accessor origin) {
		Objects.requireNonNull(origin);
		
		MongoAccessor result = new MongoAccessor();
		result.setName(origin.getName());
		result.setPermit(origin.getPermit());
		result.setAlias(origin.isAlias());
		result.setSvc(origin.isSvc());
		result.setOrgLevels(origin.getOrgLevels());
		result.setXPermits(origin.getXPermits());

		return result;
	}
}
