package core.utils;

/**
 * TODO: oh god.
 * https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea&tname=subj-all-departments
 */
public interface FacultyTreeNodeIf {

    String getName();

    FacultyTreeNodeType getType();

    /**
     * I've never understood what the deal was with
     * "school", "institute", and "centre". Sheesh.
     */
    public enum FacultyTreeNodeType {
        FACULTY,
        SCHOOL,
        // INSTITUTE,
        // CENTRE,
        DEPARTMENT,
        ;
    }

}

