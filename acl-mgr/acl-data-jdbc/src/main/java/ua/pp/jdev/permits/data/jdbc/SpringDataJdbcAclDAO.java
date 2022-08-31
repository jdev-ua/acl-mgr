package ua.pp.jdev.permits.data.jdbc;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListPageableDAO;

@Slf4j
@Component
public class SpringDataJdbcAclDAO implements AccessControlListPageableDAO {
	private JdbcAclRepository repository;

	public SpringDataJdbcAclDAO(JdbcAclRepository repository) {
		log.info("Initializing ACL datasource persisting data in embedded H2 database by Spring Data JDBC");

		this.repository = repository;
	}

	@Override
	public Collection<AccessControlList> readAll() {
		return Lists.newArrayList(repository.findAll()).stream().map(TableACL::toAccessControlList)
				.sorted(Comparator.comparing(AccessControlList::getName)).collect(Collectors.toList());
	}
	
	public ua.pp.jdev.permits.data.Page<AccessControlList> readPage(int pageNo) {
		Pageable pageable = PageRequest.of(pageNo, 5, Sort.by("name").ascending());
		Page<TableACL> page = repository.findAll(pageable);
		return new ua.pp.jdev.permits.data.Page<AccessControlList>() {
			@Override
			public int getPageCount() {
				return page.getTotalPages();
			}

			@Override
			public long getItemCount() {
				return page.getTotalElements();
			}

			@Override
			public Collection<AccessControlList> getContent() {
				return page.map(TableACL::toAccessControlList).getContent();
			}
		};
	}
	
	@Override
	public Optional<AccessControlList> read(String id) {
		Optional<TableACL> result = repository.findById(Long.parseLong(id));
		return result.isPresent() ? Optional.of(TableACL.toAccessControlList(result.get())) : Optional.empty();
	}

	@Override
	public void create(AccessControlList acl) {
		repository.save(TableACL.fromAccessControlList(acl));
	}

	@Override
	public void update(AccessControlList acl) {
		repository.save(TableACL.fromAccessControlList(acl));
	}

	@Override
	public boolean delete(String id) {
		repository.deleteById(Long.parseLong(id));
		return true;
	}
}
