package org.bse.data.repr;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class Professor {

    public static final Map<String, Professor> PROF_FULL_NAME_MAP;
    static {
        final int initialCapacity = 30;
        PROF_FULL_NAME_MAP = new HashMap<>(initialCapacity);
    }

    private final String firstName;
    private final String lastName;

    public Professor fromXml(Element profElement) {
        // get first name and last name, and check if a prof by that full name
        // already exists in [PROF_FULL_NAME_MAP]. If so, return that prof, and
        // if not, create one, register it to the map, and return it.
        return null; // TODO:
    }

    // No public XML constructor because of the static registry.
    private Professor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }



    public enum Xml {

    }

}
