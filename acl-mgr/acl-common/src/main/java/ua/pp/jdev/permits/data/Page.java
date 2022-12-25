package ua.pp.jdev.permits.data;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Defines a simple object to navigate though entire collection by pages -
 * smaller portions of containing items.
 * 
 * @author Maksym Shramko
 *
 * @param <T>
 */
public interface Page<T> {
	/**
	 * Returns the number of the current {@link Page}. Is always non-negative.
	 *
	 * @return the number of the current {@link Page}.
	 */
	int getNumber();

	/**
	 * Returns the size of the {@link Page}.
	 *
	 * @return the size of the {@link Page}.
	 */
	int getSize();
	
	/**
	 * Returns the number of total pages.
	 *
	 * @return the number of total pages
	 */
	int getPageCount();

	/**
	 * Returns the total amount of items.
	 *
	 * @return the total amount of items
	 */
	long getItemCount();

	/**
	 * Returns the page content as {@link Collection}.
	 *
	 * @return the page content
	 */
	Collection<T> getContent();

	/**
	 * Creates a new {@link Page} from the given data
	 * 
	 * @param data     a collection of items to create page, must not be
	 *                 {@code null}.
	 * @param pageNo   zero-based page index, must not be negative.
	 * @param pageSize the size of the page to be returned, must be greater than 0.
	 * @return a new {@code Page}
	 * @throws {@link NullPointerException} if {@code data} is {@code null};
	 *                {@link IllegalArgumentException} if page index is negative or
	 *                page size less than 1
	 */
	static <T> Page<T> of(Collection<T> data, int pageNo, int pageSize) {
		Objects.requireNonNull(data);
		
		if (pageNo < 0) {
			throw new IllegalArgumentException("Page number must not be negative");
		}

		if (pageSize < 1) {
			throw new IllegalArgumentException("Page size must be greater than 0");
		}
		
		List<T> pageContent = data.stream().skip(pageNo * pageSize).limit(pageSize).toList();

		return of(pageContent, pageNo, pageSize, data.size(),
				pageSize == 0 ? 1 : (int) Math.ceil((double) data.size() / (double) pageSize));
	}

	/**
	 * Creates a new {@link Page}
	 * 
	 * @param data      a collection of items to create page, must not be
	 *                  {@code null}.
	 * @param pageNo    zero-based page index, must not be negative.
	 * @param pageSize  the size of the page to be returned, must be greater than 0.
	 * @param itemCount a total amount of items, must not be negative.
	 * @param pageCount a number of total pages, must be greater than 0.
	 * @return a new {@code Page}
	 * @throws {@link NullPointerException} if {@code pageContent} is {@code null};
	 *                {@link IllegalArgumentException} if page index is negative, or
	 *                page size less than 1, or amount of items is negative, or
	 *                number of total pages less than 1
	 */
	static <T> Page<T> of(Collection<T> pageContent, int pageNo, int pageSize, int itemCount, int pageCount) {
		Objects.requireNonNull(pageContent);

		if (pageNo < 0) {
			throw new IllegalArgumentException("Page number must not be negative");
		}

		if (pageSize < 1) {
			throw new IllegalArgumentException("Page size must be greater than 0");
		}

		if (itemCount < 0) {
			throw new IllegalArgumentException("Item count must not be negative");
		}
		
		if (pageCount < 1) {
			throw new IllegalArgumentException("Page size must be greater than 0");
		}
		
		return new Page<T>() {
			@Override
			public int getPageCount() {
				return pageCount;
			}

			@Override
			public long getItemCount() {
				return itemCount;
			}

			@Override
			public Collection<T> getContent() {
				return pageContent;
			}

			@Override
			public int getNumber() {
				return pageNo;
			}

			@Override
			public int getSize() {
				return pageSize;
			}
		};
	}
}
