package ua.pp.jdev.permits.data.cassandra;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;

@Lazy
@Component
public class CassandraAclDAO implements AclDAO {
	private CassandraAclRepository repository;

	public CassandraAclDAO(CassandraAclRepository repository) {
		this.repository = repository;
	}

	@Override
	public Collection<Acl> readAll() {
		return Lists.newArrayList(repository.findAll()).stream().map(CassandraACL::toAcl)
				.sorted(Comparator.comparing(Acl::getName)).collect(Collectors.toList());
	}

	@Override
	public Optional<Acl> read(String id) {
		Optional<CassandraACL> result = repository.findById(UUID.fromString(id));
		return result.isPresent() ? Optional.of(result.get().toAcl()) : Optional.empty();
	}

	@Override
	public Acl create(Acl acl) {
		return repository.save(CassandraACL.of(acl)).toAcl();
	}

	@Override
	public Acl update(Acl acl) {
		return repository.save(CassandraACL.of(acl)).toAcl();
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(UUID.fromString(id));
		return true;
	}

	@Override
	public Optional<Acl> readByName(String name) {
		Optional<CassandraACL> result = repository.findByName(name).stream().findFirst();
		return result.isPresent() ? Optional.of(result.get().toAcl()) : Optional.empty();
	}
}
