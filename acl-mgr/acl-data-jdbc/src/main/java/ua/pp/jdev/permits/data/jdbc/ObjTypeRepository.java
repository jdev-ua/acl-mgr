package ua.pp.jdev.permits.data.jpa;

import org.springframework.data.repository.CrudRepository;

interface ObjTypeRepository extends CrudRepository<TableObjType, Long> {
	Iterable<TableObjType> findAllByAclId(Long aclId);
}
