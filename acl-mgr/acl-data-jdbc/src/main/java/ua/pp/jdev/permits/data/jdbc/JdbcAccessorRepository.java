package ua.pp.jdev.permits.data.jdbc;

import org.springframework.data.repository.CrudRepository;

interface JdbcAccessorRepository extends CrudRepository<TableAccessor, Long> {
	Iterable<TableAccessor> findAllByAclId(Long aclId);
}
