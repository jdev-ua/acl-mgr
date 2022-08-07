package ua.pp.jdev.permits.enums;


import java.util.Arrays;
import java.util.Optional;

public enum OrgLevel {
    CO("CO", "Central Office (CO)"),
    VR("VR", "Super Region (VR)"),
    MR("MR", "Macro Region (MR)"),
    RD("RD", "Regional Direction (RD)"),
    OD("OD", "Outstanding Direction (OD)");

    private final String value;
    private final String display;

    OrgLevel(String value, String display) {
        this.value = value;
        this.display = display;
    }

    public String getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    public static Optional<OrgLevel> getOrgLevel(String value) {
        return Arrays.stream(OrgLevel.values()).filter(orgLevel -> orgLevel.getValue().equalsIgnoreCase(value)).findFirst();
    }
}
