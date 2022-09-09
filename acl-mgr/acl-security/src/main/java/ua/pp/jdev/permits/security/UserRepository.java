package ua.pp.jdev.permits.security;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends PagingAndSortingRepository<UserDetailsImpl, Long> {
	Optional<UserDetailsImpl> findByUsername(String username);
}
