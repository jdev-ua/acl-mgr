package ua.pp.jdev.permits.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ua.pp.jdev.permits.domain.AccessControlList;

@Repository
public interface AccessContolListRepository extends CrudRepository<AccessControlList, Long> {

}
