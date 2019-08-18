package com.dvf.ucst.data.courseutils;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class Professor {

    private static final Map<String, Professor> MODIFIABLE_PROF_FULL_NAME_MAP;
    public static final Map<String, Professor> PROF_FULL_NAME_MAP;
    static {
        final int initialCapacity = 30;
        MODIFIABLE_PROF_FULL_NAME_MAP = new HashMap<>(initialCapacity);
        PROF_FULL_NAME_MAP = Collections.unmodifiableMap(MODIFIABLE_PROF_FULL_NAME_MAP);
    }

    private final String firstName;
    private final String lastName;

    Professor(final Element profElement) throws MalformedXmlDataException {
        this.firstName = XmlUtils.getMandatoryAttr(profElement, Xml.FIRST_NAME_ATTR).getValue();
        this.lastName  = XmlUtils.getMandatoryAttr(profElement, Xml.LAST_NAME_ATTR).getValue();

        final String fullName = firstName + " " + lastName;
        MODIFIABLE_PROF_FULL_NAME_MAP.putIfAbsent(fullName, this);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public enum Xml implements XmlUtils.XmlConstant {
        PROFESSOR_TAG ("Instructor"),
        FIRST_NAME_ATTR ("firstName"),
        LAST_NAME_ATTR ("lastName"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
