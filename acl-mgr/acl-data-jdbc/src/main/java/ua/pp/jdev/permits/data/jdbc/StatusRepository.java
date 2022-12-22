package ua.pp.jdev.permits.data.jpa;

import org.springframework.data.repository.CrudRepository;

interface StatusRepository extends CrudRepository<TableStatus, Long> {
	Iterable<TableStatus> findAllByAclId(Long aclId);
}
