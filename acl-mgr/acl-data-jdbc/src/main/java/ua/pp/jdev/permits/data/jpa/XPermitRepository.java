package ua.pp.jdev.permits.data.jpa;

import org.springframework.data.repository.CrudRepository;

interface XPermitRepository extends CrudRepository<TableXPermit, Long> {
	Iterable<TableXPermit> findAllByAccessorId(Long accessorId);
}
