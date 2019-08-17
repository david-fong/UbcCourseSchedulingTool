package org.bse.data.faculties.vancouver;

import org.bse.data.courseutils.Course;
import org.bse.data.faculties.FacultyTreeNode;
import org.bse.data.faculties.FacultyTreeRootCampus;

import java.util.Map;

import static org.bse.data.faculties.FacultyTreeNode.FacultyTreeNodeType.FACULTY;

/**
 * We will go by the hierarchy specified in the second link.
 * https://www.ubc.ca/our-campuses/vancouver/directories/faculties-schools.html
 * https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea&tname=subj-all-departments
 */
public enum VancouverFaculties implements FacultyTreeNode {
    // TODO [data]: add entries as needed. prefer alphabetical order.
    APSC (FACULTY, "Applied Science", AppliedScienceFaculties.class),
    // ...
    SCIE (FACULTY, "Science", ScienceFaculties.class),
    // ...
    ;
    private final FacultyTreeNodeType type;
    private final String name;
    private final Class<? extends FacultyTreeNode> childrenClass;

    <T extends Enum & FacultyTreeNode> VancouverFaculties
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
    public FacultyTreeNode getParentNode() {
        return FacultyTreeRootCampus.UbcCampuses.VANCOUVER;
    }

    @Override
    public FacultyTreeNode[] getChildren() {
        return childrenClass.getEnumConstants();
    }

    @Override
    public Map<String, Course> getCourseIdTokenToCourseMap() {
        return null;
    }

}
