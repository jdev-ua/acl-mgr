package ua.pp.jdev.permits.data.cassandra;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CassandraAclRepository extends CrudRepository<CassandraACL, UUID> {
}
