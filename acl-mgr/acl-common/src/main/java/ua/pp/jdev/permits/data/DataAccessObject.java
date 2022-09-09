package ua.pp.jdev.permits.data;

import java.util.Collection;
import java.util.Optional;

public interface DataAccessObject<T> {
	Collection<T> readAll();

	Optional<T> read(String id);

	void create(T t);

	void update(T t);

	boolean delete(String id);
	
	default boolean pageable() {
		return false;
	}
	
	default Page<T> readPage(int page, int size) {
		return new Page<T>() {
			Collection<T> result = readAll();
			
			@Override
			public int getPageCount() {
				return 1;
			}

			@Override
			public long getItemCount() {
				return result.size();
			}

			@Override
			public Collection<T> getContent() {
				return result;
			}
		};
	}
}
