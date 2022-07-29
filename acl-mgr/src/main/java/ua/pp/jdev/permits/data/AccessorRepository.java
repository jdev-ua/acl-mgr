package ua.pp.jdev.permits.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ua.pp.jdev.permits.domain.Accessor;

@Repository
public interface AccessorRepository extends CrudRepository<Accessor, Long> {
	Iterable<Accessor> findAllByAclId(Long aclId);
}
