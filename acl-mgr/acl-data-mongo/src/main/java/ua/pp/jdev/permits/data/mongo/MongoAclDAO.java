package ua.pp.jdev.permits.data.mongo;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;

@Slf4j
@Component
public class MongoAclDAO implements AccessControlListDAO {
	private MongoAclRepository repository;

	public MongoAclDAO(MongoAclRepository repository) {
		log.info("Initializing ACL datasource persisting data in MongoDB database by Spring Data");

		this.repository = repository;
	}

	@Override
	public Collection<AccessControlList> readAll() {
		return Lists.newArrayList(repository.findAll()).stream().map(MongoACL::toAccessControlList)
				.sorted(Comparator.comparing(AccessControlList::getName)).collect(Collectors.toList());
	}

	@Override
	public Optional<AccessControlList> read(String id) {
		Optional<MongoACL> result = repository.findById(id);
		return result.isPresent() ? Optional.of(MongoACL.toAccessControlList(result.get())) : Optional.empty();
	}

	@Override
	public void create(AccessControlList acl) {
		repository.save(MongoACL.fromAccessControlList(acl));
	}

	@Override
	public void update(AccessControlList acl) {
		repository.save(MongoACL.fromAccessControlList(acl));
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(id);
		return true;
	}

}
