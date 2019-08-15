package org.bse.data.repr.faculties;

/**
 *
 */
public class CampusNotFoundException extends Exception {

    public CampusNotFoundException(String campusSearchName) {
        super(String.format("A %s by the name \"%s\" could not be found.",
                FacultyTreeNode.class.getName(), campusSearchName
        ));
    }

}
