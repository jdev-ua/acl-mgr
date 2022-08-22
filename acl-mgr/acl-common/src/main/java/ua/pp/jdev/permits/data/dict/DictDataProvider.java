package ua.pp.jdev.permits.data.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * Provides access to the dictionaries data associated with application.
 */
public final class DictDataProvider {
	/**
	 * Map of supported object types
	 */
	private Map<String, String> objTypes = new TreeMap<>();
	
	/**
	 * Map of supported user roles
	 */
	private Map<String, String> roles = new TreeMap<>();
	
	/**
	 * Map of supported service aliases
	 */
	private Map<String, String> svcAliases = new TreeMap<>();
	
	/**
	 * Map of supported not-service aliases
	 */
	private Map<String, String> nonSvcAliases = new TreeMap<>();
	
	/**
	 * Contains enumeration of maps with available statuses mapped by combination
	 * of type and service attributes
	 */
	private Map<String, Map<String, String>> statuses = new HashMap<>();

	/**
	 * Initializes provider instance with data from configuration file
	 */
	public DictDataProvider(String dataSource) throws FileNotFoundException, URISyntaxException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		// Notify builder to support dashes in field names
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);
		// Create a new Gson instance with your customized configuration
		Gson gson = gsonBuilder.create();
		
		// Get an URL that refers on configuration file
		URL url = this.getClass().getResource(dataSource);
		BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI())));
		// Create new JsonReader
		JsonReader jsonReader = new JsonReader(br);
		// Read data from configuration file
		DictRoot data = gson.fromJson(jsonReader, DictRoot.class);
		// Get an array of all supported object types and convert it into the list
		objTypes.putAll(
				Arrays.stream(data.typesAll).collect(Collectors.toMap(Function.identity(), Function.identity())));
		// Get an array of all supported user roles and convert it into the list
		roles.putAll(Arrays.stream(data.rolesAll).collect(Collectors.toMap(Function.identity(), Function.identity())));

		// Split an array of all supported aliases into two separated maps
		// depending on service sign of every item
		svcAliases.putAll(Arrays.stream(data.aliasesAll).filter(t -> t.service)
				.collect(Collectors.toMap(k -> k.alias, v -> v.alias)));
		nonSvcAliases.putAll(Arrays.stream(data.aliasesAll).filter(t -> !t.service)
				.collect(Collectors.toMap(k -> k.alias, v -> v.alias)));

		// Populate map with all available statuses
		statuses.putAll(Arrays.stream(data.statusesAll).collect(Collectors.toMap(k -> buildKey(k.type, k.service),
				v -> Arrays.stream(v.statuses).collect(Collectors.toMap(Function.identity(), Function.identity())),
				(t, u) -> {
					t.putAll(u);
					return t;
				}, TreeMap::new)));
	}

	/**
	 * Returns a {@code String} with a key as a result of input parameters conjunction.
	 *
	 * @param objType    an object type name
	 * @param serviceCode a service code
	 * @return a {@code String} with a generated key
	 */
	private static String buildKey(String objType, String serviceCode) {
		// Validate input parameter
		Objects.requireNonNull(objType, "Type parameter is required!");
		// Build a key
		StringBuilder result = new StringBuilder(objType);
		// TODO Reserved for future!
		/*
		 * if (serviceCode != null && serviceCode.length() > 0)
		 * { result.append("::").append(service); }
		 */

		return result.toString();
	}

	/**
	 * Returns a {@code Map} of object types supported by application.
	 *
	 * @return a {@code Map} of object types
	 */
	public Map<String, String> getSupportedTypes() {
		return Collections.unmodifiableMap(objTypes);
	}

	/**
	 * Returns a {@code Map} of user roles supported by application.
	 *
	 * @return a {@code Map} of user roles
	 */
	public Map<String, String> getSupportedRoles() {
		return Collections.unmodifiableMap(roles);
	}

	/**
	 * Returns a {@code Map} of aliases supported by application.
	 *
	 * @param service whether aliases should have service sign or not
	 * @return a {@code Map} of aliases
	 */
	public Map<String, String> getSupportedAliases(boolean service) {
		return Collections.unmodifiableMap(service ? svcAliases : nonSvcAliases);
	}

	/**
	 * Returns a {@code Map} of statuses available for current combination of input parameters.
	 *
	 * @param objType    an object type name
	 * @param serviceCode a service code
	 * @return a {@code Map} of available statuses
	 */
	public Map<String, String> getAvailableStatuses(String objType, String serviceCode) {
		return Collections.unmodifiableMap(statuses.getOrDefault(buildKey(objType, serviceCode), new HashMap<>()));
	}
}
