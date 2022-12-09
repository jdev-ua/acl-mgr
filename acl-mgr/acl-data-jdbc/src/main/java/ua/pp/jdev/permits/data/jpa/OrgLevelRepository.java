package ua.pp.jdev.permits.data.jpa;

import org.springframework.data.repository.CrudRepository;

interface OrgLevelRepository extends CrudRepository<TableOrgLevel, Long> {
	Iterable<TableOrgLevel> findAllByAccessorId(Long accessorId);
}
