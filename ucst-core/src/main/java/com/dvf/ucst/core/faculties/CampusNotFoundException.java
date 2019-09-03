package com.dvf.ucst.core.faculties;

/**
 *
 */
public class CampusNotFoundException extends Exception {

    public CampusNotFoundException(final String campusSearchName) {
        super(String.format("A %s by the name \"%s\" could not be found.",
                FacultyTreeNode.class, campusSearchName
        ));
    }

}
