package ua.pp.jdev.permits.data.orm.cassandra;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CassandraAclRepository extends CrudRepository<CassandraACL, UUID> {
}
