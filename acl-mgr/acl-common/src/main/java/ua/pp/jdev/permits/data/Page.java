package ua.pp.jdev.permits.data;

import java.util.Collection;

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
	 * @return
	 */
	Collection<T> getContent();
}
