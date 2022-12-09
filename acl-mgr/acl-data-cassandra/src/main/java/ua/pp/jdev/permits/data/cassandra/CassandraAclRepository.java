package ua.pp.jdev.permits.data.cassandra;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

interface CassandraAclRepository extends CrudRepository<CassandraACL, UUID> {
	List<CassandraACL> findByName(String name);
}
