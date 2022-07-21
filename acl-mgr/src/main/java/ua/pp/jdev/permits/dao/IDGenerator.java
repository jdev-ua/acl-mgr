package ua.pp.jdev.permits.dao;

import java.util.UUID;

public final class IDGenerator {
    public synchronized static Long generateID() {
    	return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
