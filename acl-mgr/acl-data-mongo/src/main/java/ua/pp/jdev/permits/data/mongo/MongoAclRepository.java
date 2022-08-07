package ua.pp.jdev.permits.data.mongo;

import org.springframework.data.repository.CrudRepository;

interface MongoAclRepository extends CrudRepository<MongoACL, String> {

}
