package ua.pp.jdev.permits.dao;

final class IDGenerator {
	public final static String NULL_ID = "00000000";

    private static int lastId = 0;

    public synchronized static String generateID(String pattern) {
        return String.format(pattern, ++lastId);
    }
}
