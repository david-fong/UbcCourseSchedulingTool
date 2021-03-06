package com.dvf.ucst.core.faculties;

/**
 * Thrown when a [Course] is attempted to be found under a [FacultyTreeNodeIf], and
 * the [Course] cannot be found.
 */
public final class FacultyCourseNotFoundException extends Exception {

    public FacultyCourseNotFoundException(String suppliedSearchCode, FacultyTreeNode facultyNode) {
        this(suppliedSearchCode, facultyNode, null);
    }

    public FacultyCourseNotFoundException(
            final String suppliedSearchCode,
            final FacultyTreeNode facultyNode,
            final Exception rootCause
    ) {
        super(String.format("A course by the code \"%s\" could not be found under"
                + " the %s \"%s\"",
                suppliedSearchCode,
                FacultyTreeNode.class,
                facultyNode.getNameWithTitle()
        ), rootCause);
    }

}
