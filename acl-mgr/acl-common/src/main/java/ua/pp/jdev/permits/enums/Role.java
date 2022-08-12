package ua.pp.jdev.permits.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
	/** Defines role with administrator privileges. */
	ADMIN("ROLE_ADMIN", "Administrator", "ADMIN"),
	/** Defines role with full privileges for ACLs. */
	EDITOR("ROLE_EDITOR", "Editor", "EDITOR"),
	/** Defines role with read only privilege for ACLs. */
	VIEWER("ROLE_VIEWER", "Viewer", "VIEWER");

	private final String fullName;
	private final String display;
	private final String name;

	private Role(String fullName, String display, String name) {
		this.fullName = fullName;
		this.display = display;
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public String getDisplay() {
		return display;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns an Optional with Role which name or full name matches specified qualifier or empty value otherwise.  
	 * @param qualifier name or full name
	 * @return an optional result
	 */
	public static Optional<Role> getRole(String qualifier) {
		return Arrays.stream(Role.values()).filter(
				role -> role.getFullName().equalsIgnoreCase(qualifier) || role.getName().equalsIgnoreCase(qualifier))
				.findFirst();
	}
}
