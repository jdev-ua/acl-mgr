package ua.pp.jdev.permits.data.orm.cassandra;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

interface CassandraAclRepository extends CrudRepository<CassandraACL, UUID> {
}
