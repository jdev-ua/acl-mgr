package ua.pp.jdev.permits.data.jpa;

import org.springframework.data.repository.CrudRepository;

interface AccessorRepository extends CrudRepository<TableAccessor, Long> {
	Iterable<TableAccessor> findAllByAclId(Long aclId);
}
