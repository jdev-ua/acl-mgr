package ua.pp.jdev.permits.util;

import java.util.UUID;

public final class IDGenerator {
	public synchronized static Long genLongID() {
		return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
	}

	public synchronized static String genStringID() {
		return String.valueOf(genLongID());
	}
}
