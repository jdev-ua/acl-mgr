package ua.pp.jdev.permits.data.jdbc;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;

@Lazy
@Component
public class SpringDataJdbcAclDAO implements AclDAO {
	private AclRepository repository;

	public SpringDataJdbcAclDAO(AclRepository repository) {
		this.repository = repository;
	}

	@Override
	public Collection<Acl> readAll() {
		return Lists.newArrayList(repository.findAll()).stream().map(TableACL::toAcl)
				.sorted(Comparator.comparing(Acl::getName)).collect(Collectors.toList());
	}
	
	@Override
	public ua.pp.jdev.permits.data.Page<Acl> readPage(int pageNo, int size) {
		Pageable pageable = PageRequest.of(pageNo, size, Sort.by("name").ascending());
		Page<TableACL> page = repository.findAll(pageable);
		return new ua.pp.jdev.permits.data.Page<Acl>() {
			@Override
			public int getPageCount() {
				return page.getTotalPages();
			}

			@Override
			public long getItemCount() {
				return page.getTotalElements();
			}

			@Override
			public Collection<Acl> getContent() {
				return page.map(TableACL::toAcl).getContent();
			}
		};
	}
	
	@Override
	public Optional<Acl> read(String id) {
		Optional<TableACL> result = repository.findById(Long.parseLong(id));
		return result.isPresent() ? Optional.of(result.get().toAcl()) : Optional.empty();
	}

	@Override
	public Acl create(Acl acl) {
		return repository.save(TableACL.of(acl)).toAcl();
	}

	@Override
	public Acl update(Acl acl) {
		return repository.save(TableACL.of(acl)).toAcl();
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(Long.parseLong(id));
		return true;
	}

	@Override
	public Optional<Acl> readByName(String name) {
		Optional<TableACL> result = repository.findByName(name).stream().findFirst();
		return result.isPresent() ? Optional.of(result.get().toAcl()) : Optional.empty();
	}
}
