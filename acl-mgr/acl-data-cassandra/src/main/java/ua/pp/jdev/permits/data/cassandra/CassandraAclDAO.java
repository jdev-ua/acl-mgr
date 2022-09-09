package ua.pp.jdev.permits.data.cassandra;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;

@Slf4j
@Component
public class CassandraAclDAO implements AccessControlListDAO {
	private CassandraAclRepository repository;

	public CassandraAclDAO(CassandraAclRepository repository) {
		log.info("Initializing ACL datasource persisting data in Apache Cassandra database by Spring Data");

		this.repository = repository;
	}

	@Override
	public Collection<AccessControlList> readAll() {
		return Lists.newArrayList(repository.findAll()).stream().map(CassandraACL::toAccessControlList)
				.sorted(Comparator.comparing(AccessControlList::getName)).collect(Collectors.toList());
	}

	@Override
	public Optional<AccessControlList> read(String id) {
		Optional<CassandraACL> result = repository.findById(UUID.fromString(id));
		return result.isPresent() ? Optional.of(CassandraACL.toAccessControlList(result.get())) : Optional.empty();
	}

	@Override
	public void create(AccessControlList acl) {
		repository.save(CassandraACL.fromAccessControlList(acl));
	}

	@Override
	public void update(AccessControlList acl) {
		repository.save(CassandraACL.fromAccessControlList(acl));
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(UUID.fromString(id));
		return true;
	}

	@Override
	public Optional<AccessControlList> readByName(String name) {
		Optional<CassandraACL> result = repository.findByName(name).stream().findFirst();
		return result.isPresent() ? Optional.of(CassandraACL.toAccessControlList(result.get())) : Optional.empty();
	}
}
