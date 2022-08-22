package ua.pp.jdev.permits.data.dict;

/**
 * Provides simple realization of root Properties Bag to deserialize dictionaries data from JSON config with Gson.
 */
class DictRoot {
    /**
     * Refers on "types-all" array of strings
     */
    String[] typesAll;
    /**
     * Refers on "statuses-all" array of inner object
     */
    Status[] statusesAll;
    /**
     * Refers on "roles-all" array of strings
     */
    String[] rolesAll;
    /**
     * Refers on "aliases-all" array of strings
     */
    Alias[] aliasesAll;
}
