package ua.pp.jdev.permits.enums;


import java.util.Arrays;
import java.util.Optional;

public enum Permit {
    NONE(1, "None"),
    BROWSE(2, "Browse"),
    READ(3, "Read"),
    RELATE(4, "Relate"),
    VERSION(5, "Version"),
    WRITE(6, "Write"),
    DELETE(7, "Delete");

    private final int value;
    private final String display;

    Permit(int value, String display) {
        this.value = value;
        this.display = display;
    }

    public int getValue() {
        return value;
    }

    public String getDisplay() {
        return display;
    }

    public static Optional<Permit> getPermit(int value) {
        return Arrays.stream(Permit.values()).filter(permit -> permit.getValue() == value).findFirst();
    }
}
