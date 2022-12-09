package ua.pp.jdev.permits.data.mongo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
@Document(collection = "acls")
@NoArgsConstructor
class MongoACL implements Serializable {
	private static final long serialVersionUID = 4871380951528686011L;

	@Id
	private String id;

	private String name;
	private String description;

	private Set<String> statuses = new HashSet<>();
	private Set<String> objTypes = new HashSet<>();
	private Set<MongoAccessor> accessors = new HashSet<>();

	@PersistenceCreator
	protected MongoACL(String id, String name, String description, Set<String> statuses, Set<String> objTypes,
			Set<MongoAccessor> accessors) {
		setName(name);
		setId(id);
		setDescription(description);
		setStatuses(statuses);
		setObjTypes(objTypes);
		setAccessors(accessors);
	}

	public Acl toAcl() {
		return Acl.builder()
				.id(getId().toString())
				.name(getName())
				.description(getDescription())
				.statuses(getStatuses())
				.objTypes(getObjTypes())
				.accessors(getAccessors().stream().map(MongoAccessor::toAccessor).collect(Collectors.toSet()))
				.build();
	}

	public static MongoACL of(Acl origin) {
		Objects.requireNonNull(origin);

		MongoACL result = new MongoACL();
		// For newly created ACL skip ID setup to avoid possible errors
		// while parsing a value generated outside Cassandra
		if (IDGenerator.validateID(origin.getId())) {
			result.setId(origin.getId());
		}
		result.setName(origin.getName());
		result.setDescription(origin.getDescription());
		result.setStatuses(origin.getStatuses());
		result.setObjTypes(origin.getObjTypes());
		result.setAccessors(
				origin.getAccessors().stream().map(MongoAccessor::fromAccessor).collect(Collectors.toSet()));

		return result;
	}
}
