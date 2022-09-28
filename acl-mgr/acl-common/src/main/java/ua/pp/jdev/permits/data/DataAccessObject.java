package ua.pp.jdev.permits.data;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines base Data Access Object (DAO) interface.
 * 
 * @author Maksym Shramko
 *
 */
public interface DataAccessObject<T> {
	/**
	 * Returns a {@link Collection} of all entities.
	 * 
	 * @return a {@code Collection} of all entities
	 */
	Collection<T> readAll();

	/**
	 * Retrieves an entity by its id.
	 * 
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none
	 *         found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	Optional<T> read(String id);

	/**
	 * Creates a new entity.
	 * 
	 * @param entity must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is
	 *                                  {@literal null}.
	 */
	void create(T t);

	/**
	 * Updates a given entity.
	 * 
	 * @param entity must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is
	 *                                  {@literal null}.
	 */
	void update(T t);

	/**
	 * Deletes the entity with the given id.
	 * 
	 * @param id an id of entity
	 * @return whether entity was deleted successfully or not
	 */
	boolean delete(String id);

	/**
	 * Returns a {@link Page} of entities.
	 * 
	 * @param pageNo   zero-based page index, must not be negative.
	 * @param pageSize the size of the page to be returned, must be greater than 0.
	 * @return a page of entities
	 * @throws {@link IllegalArgumentException} if page index is negative or page
	 *                size less than 1
	 */
	default Page<T> readPage(int pageNo, int pageSize) {
		if (pageNo < 0) {
			throw new IllegalArgumentException("Page number must not be negative");
		}

		if (pageSize < 1) {
			throw new IllegalArgumentException("Page size must be greater than 0");
		}
		
		return Page.of(readAll(), pageNo, pageSize);
	}
}
