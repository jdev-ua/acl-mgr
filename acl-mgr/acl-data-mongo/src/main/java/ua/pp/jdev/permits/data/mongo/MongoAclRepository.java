package ua.pp.jdev.permits.data.mongo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

interface MongoAclRepository extends CrudRepository<MongoACL, String> {
	List<MongoACL> findByName(String name);
}
