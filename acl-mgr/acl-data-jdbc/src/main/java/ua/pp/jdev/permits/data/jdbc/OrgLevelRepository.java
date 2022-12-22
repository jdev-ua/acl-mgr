package ua.pp.jdev.permits.data.jdbc;

import org.springframework.data.repository.CrudRepository;

interface OrgLevelRepository extends CrudRepository<TableOrgLevel, Long> {
	Iterable<TableOrgLevel> findAllByAccessorId(Long accessorId);
}
