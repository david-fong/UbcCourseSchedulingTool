package org.bse.data.repr;

import org.bse.utils.xml.XmlParsingUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class Professor {

    private static final Map<String, Professor> PROF_FULL_NAME_MAP;
    static {
        final int initialCapacity = 30;
        PROF_FULL_NAME_MAP = new HashMap<>(initialCapacity);
    }

    private final String firstName;
    private final String lastName;

    // TODO [xml:read]
    public Professor(Element profElement) {
        // get first name and last name, and check if a prof by that full name
        // already exists in [PROF_FULL_NAME_MAP]. If so, return that prof, and
        // if not, create one, register it to the map, and return it.
        this.firstName = null;
        this.lastName = null;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public enum Xml implements XmlParsingUtils.XmlConstant {
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

}
