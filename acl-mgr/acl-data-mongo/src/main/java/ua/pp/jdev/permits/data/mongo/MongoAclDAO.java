package ua.pp.jdev.permits.data.mongo;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;

@Lazy
@Component
public class MongoAclDAO implements AclDAO {
	private MongoAclRepository repository;

	public MongoAclDAO(MongoAclRepository repository) {
		this.repository = repository;
	}

	@Override
	public Collection<Acl> readAll() {
		return Lists.newArrayList(repository.findAll()).stream().map(MongoACL::toAcl)
				.sorted(Comparator.comparing(Acl::getName)).collect(Collectors.toList());
	}

	@Override
	public Optional<Acl> read(String id) {
		Optional<MongoACL> result = repository.findById(id);
		return result.isPresent() ? Optional.of(result.get().toAcl()) : Optional.empty();
	}

	@Override
	public Acl create(Acl acl) {
		return repository.save(MongoACL.of(acl)).toAcl();
	}

	@Override
	public Acl update(Acl acl) {
		return repository.save(MongoACL.of(acl)).toAcl();
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(id);
		return true;
	}

	@Override
	public Optional<Acl> readByName(String name) {
		Optional<MongoACL> result = repository.findByName(name).stream().findFirst();
		return result.isPresent() ? Optional.of(result.get().toAcl()) : Optional.empty();
	}
}
