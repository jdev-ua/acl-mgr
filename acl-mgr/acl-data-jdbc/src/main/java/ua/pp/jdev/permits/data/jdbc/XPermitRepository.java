package ua.pp.jdev.permits.data.jdbc;

import org.springframework.data.repository.CrudRepository;

interface XPermitRepository extends CrudRepository<TableXPermit, Long> {
	Iterable<TableXPermit> findAllByAccessorId(Long accessorId);
}
