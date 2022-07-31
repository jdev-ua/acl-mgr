package ua.pp.jdev.permits.dao;

import java.util.Collection;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AccessContolListRepository;
import ua.pp.jdev.permits.domain.AccessControlList;

@Slf4j
@Component
@Profile("spring-data-jdbc")
public class SpringDataJdbcAclDAO implements AccessControlListDAO {
	private AccessContolListRepository repository;

	public SpringDataJdbcAclDAO(AccessContolListRepository repository) {
		log.info("Initializing ACL datasource persisting data in embedded H2 database by Spring Data JDBC");
		
		this.repository = repository;
	}

	@Override
	public Collection<AccessControlList> readAll() {
		return Lists.newArrayList(repository.findAll());
	}

	@Override
	public Optional<AccessControlList> read(String id) {
		return repository.findById(Long.parseLong(id));
	}

	@Override
	public void create(AccessControlList acl) {
		repository.save( acl);
	}

	@Override
	public void update(AccessControlList acl) {
		repository.save(acl);
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(Long.parseLong(id));
		return true;
	}
}
