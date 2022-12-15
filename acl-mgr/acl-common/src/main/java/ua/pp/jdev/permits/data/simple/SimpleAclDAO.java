package ua.pp.jdev.permits.data.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;
import ua.pp.jdev.permits.util.IDGenerator;

@Component
public class SimpleAclDAO implements AclDAO {
	private Map<String, AclRecord> storage = new HashMap<>();

	@Override
	public Collection<Acl> readAll() {
		return storage.values().stream()
				.map(rec -> Converters.convert(rec))
				.sorted((acl1, acl2) -> acl1.getName().compareToIgnoreCase(acl2.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<Acl> read(String id) {
		Objects.requireNonNull(id);
		
		return storage.containsKey(id) ? Optional.of(Converters.convert(storage.get(id))) : Optional.empty();
	}

	@Override
	public Acl create(Acl data) {
		Objects.requireNonNull(data);
		
		if (storage.containsKey(data.getId())) {
			// TODO Customize exception
			throw new RuntimeException("Creation failed: ACL with ID '" + data.getId() + "' already exists");
		}
		
		if (readByName(data.getName()).isPresent()) {
			// TODO Customize exception
			throw new RuntimeException("Creation failed: ACL with name '" + data.getId() + "' already exists");
		}
		
		return save(data);
	}

	@Override
	public Acl update(Acl data) {
		Objects.requireNonNull(data);
		
		if(!IDGenerator.validateID(data.getId())) {
			// TODO Customize exception
			throw new RuntimeException("Update failed: ACL has invalid ID '" + data.getId() + "'");
		}
		
		if (!storage.containsKey(data.getId())) {
			// TODO Customize exception
			throw new RuntimeException("Update failed: ACL with ID '" + data.getId() + "' doesn't exist");
		}
		
		return save(data);
	}
	
	private Acl save(Acl origin) {
		AclRecord rec = Converters.convert(origin);

		storage.put(rec.id(), rec);

		// TODO Customize exception
		return read(rec.id())
				.orElseThrow(() -> new RuntimeException("Failed to retrieve saved ACL '" + rec.id() + "' from storage"));
	}

	@Override
	public boolean delete(String id) {
		Objects.requireNonNull(id);
		
		return (storage.remove(id) != null);
	}

	@Override
	public Optional<Acl> readByName(String name) {
		Objects.requireNonNull(name);
		
		return storage.values().stream()
				.filter(rec -> rec.name().equalsIgnoreCase(name))
				.map(rec -> Converters.convert(rec))
				.findFirst();
	}
}
