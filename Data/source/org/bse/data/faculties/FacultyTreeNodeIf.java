package org.bse.data.faculties;

/**
 * A property of a course. This is not the same as a program.
 * A program is represented as a [CourseIf] with type [PROGRAM],
 * and a non-empty set of co-requisites, which may require courses
 * belonging to several different faculties.
 */
public interface FacultyTreeNodeIf {

    String getNameNoTitle();

    String getAbbreviation();

    FacultyTreeNodeType getType();

    /* These must return null if they do not apply.
     * Each node in the returned array of some implementation
     * "A"'s implementation of getChildren must return A.
     */
    FacultyTreeNodeIf getParentNode();
    FacultyTreeNodeIf[] getChildren();

    default FacultyTreeNodeIf recursiveGetParentNode() {
        if (getParentNode() == null) return this;
        else return getParentNode().recursiveGetParentNode();
    }

    /**
     * I've never understood what the deal was with
     * "school", "institute", and "centre". Sheesh.
     */
    enum FacultyTreeNodeType {
        FACULTY    ("Faculty of "),
        SCHOOL     ("School of "),
        // INSTITUTE,
        // CENTRE,
        DEPARTMENT ("Department of "),
        ;
        public final String title;

        FacultyTreeNodeType(String title) {
            this.title = title;
        }
    }

}
