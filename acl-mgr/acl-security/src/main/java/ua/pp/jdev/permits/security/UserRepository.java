package ua.pp.jdev.permits.security;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends CrudRepository<UserDetailsImpl, Long> {
	Optional<UserDetailsImpl> findByUsername(String username);
}
