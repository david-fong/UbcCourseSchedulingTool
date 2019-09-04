package com.dvf.ucst.core.faculties.vancouver;

import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.faculties.UbcCampuses;

import java.util.HashMap;
import java.util.Map;

import static com.dvf.ucst.core.faculties.FacultyTreeNode.FacultyTreeNodeType.FACULTY;

/**
 *
 */
public enum VancouverFaculties implements FacultyTreeNode {
    // TODO [data]: add entries as needed. prefer alphabetical order.
    APSC (FACULTY, "Applied Science", BascFaculties.class),
    // ...
    SCIE (FACULTY, "Science", ScieFaculties.class),
    // ...
    ;
    private final FacultyTreeNodeType facultyType;
    private final String nonAbbreviatedName;
    private final Class<? extends FacultyTreeNode> childrenClass;
    private final Map<String, Course> courseCodeMap;

    <T extends Enum<?> & FacultyTreeNode> VancouverFaculties(
            final FacultyTreeNodeType facultyType,
            final String nonAbbreviatedName,
            final Class<T> childrenClass
    ) {
        this.facultyType = facultyType;
        this.nonAbbreviatedName = nonAbbreviatedName;
        this.childrenClass = childrenClass;
        this.courseCodeMap = new HashMap<>();
    }

    @Override
    public String getNameNoTitle() {
        return nonAbbreviatedName;
    }

    @Override
    public String getAbbreviation() {
        return name();
    }

    @Override
    public FacultyTreeNodeType getFacultyType() {
        return facultyType;
    }

    @Override
    public UbcCampuses getParentNode() {
        return UbcCampuses.VANCOUVER;
    }

    @Override
    public FacultyTreeNode[] getChildren() {
        return childrenClass.getEnumConstants();
    }

    @Override
    public Map<String, Course> getCourseIdTokenToCourseMap() {
        return courseCodeMap;
    }

}
