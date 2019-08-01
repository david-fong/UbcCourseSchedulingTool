package org.bse.data.faculties.vancouver;

import org.bse.data.faculties.FacultyTreeNodeIf;
import org.bse.data.faculties.FacultyTreeRootNodeIf;

import static org.bse.data.faculties.FacultyTreeNodeIf.FacultyTreeNodeType.FACULTY;

/**
 * We will go by the hierarchy specified in the second link.
 * https://www.ubc.ca/our-campuses/vancouver/directories/faculties-schools.html
 * https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea&tname=subj-all-departments
 */
public enum VancouverFaculties implements FacultyTreeRootNodeIf {
    // TODO: add entries as needed. prefer alphabetical order.
    APSC (FACULTY, "Applied Science", AppliedScienceFaculties.class),
    // ...
    SCIE (FACULTY, "Science", ScienceFaculties.class),
    // ...
    ;
    private final FacultyTreeNodeType type;
    private final String name;
    private final Class<? extends FacultyTreeNodeIf> childrenClass;

    <T extends Enum & FacultyTreeNodeIf> VancouverFaculties
            (FacultyTreeNodeType type, String name, Class<T> childrenClass) {
        this.type = type;
        this.name = name;
        this.childrenClass = childrenClass;
    }

    @Override
    public String getNameNoTitle() {
        return name;
    }

    @Override
    public String getAbbreviation() {
        return name();
    }

    @Override
    public FacultyTreeNodeType getType() {
        return type;
    }

    @Override
    public FacultyTreeNodeIf[] getChildren() {
        return childrenClass.getEnumConstants();
    }

    @Override
    public String getCampusFolderName() {
        return "vancouver";
    }

}
