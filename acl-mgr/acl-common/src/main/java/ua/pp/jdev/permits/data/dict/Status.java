package ua.pp.jdev.permits.data.dict;

/**
 * Provides simple realization of inner Properties Bag to deserialize dictionaries data from JSON config with Gson.
 */
class Status {
    /**
     * Refers on "type" field
     */
    String type;
    /**
     * Refers on "statuses" array of strings
     */
    String[] statuses;
    /**
     * Refers on "service" field
     */
    String service;
}
