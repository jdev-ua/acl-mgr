package ua.pp.jdev.permits.util;

import java.util.UUID;

public final class IDGenerator {
	/**
	 * Default not a null value that have to be replaced by real one when persisting
	 */
	public final static String EMPTY_ID = "0";
	
	public synchronized static Long genLongID() {
		return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
	}

	public synchronized static String genStringID() {
		return String.valueOf(genLongID());
	}
	
	/**
	 * Checks whether specified string ID is "empty"
	 * @param id a string with ID
	 * @return {@code true} if specified ID has {@code null}, zero-length or {@code EMPTY_ID} value
	 */
	public static boolean isEmptyID(String id) {
		return id == null || id.length() == 0 || EMPTY_ID.equals(id);
	}
}
