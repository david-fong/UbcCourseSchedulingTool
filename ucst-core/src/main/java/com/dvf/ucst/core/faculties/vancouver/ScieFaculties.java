package com.dvf.ucst.core.faculties.vancouver;

import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.faculties.FacultyTreeNode;

import java.util.HashMap;
import java.util.Map;

import static com.dvf.ucst.core.faculties.FacultyTreeNode.FacultyTreeNodeType.DEPARTMENT;

public enum ScieFaculties implements FacultyTreeNode {
    // TODO [data]: add entries as needed. prefer alphabetical order.
    // ...
    CPSC (DEPARTMENT, "Computer Science"),
    // ...
    MATH (DEPARTMENT, "Mathematics"),
    // ...
    PHYS (DEPARTMENT, "Physics"),
    // ...
    ;
    private final FacultyTreeNodeType facultyType;
    private final String nonAbbreviatedName;
    private final Class<? extends FacultyTreeNode> childrenClass;
    private final Map<String, Course> courseCodeMap;

    <T extends Enum<?> & FacultyTreeNode> ScieFaculties(
            final FacultyTreeNodeType facultyType,
            final String nonAbbreviatedName,
            final Class<T> childrenClass
    ) {
        this.facultyType = facultyType;
        this.nonAbbreviatedName = nonAbbreviatedName;
        this.childrenClass = childrenClass;
        this.courseCodeMap = new HashMap<>();
    }

    // constructor for faculties without children faculties.
    ScieFaculties(final FacultyTreeNodeType facultyType, final String nonAbbreviatedName) {
        this(facultyType, nonAbbreviatedName, null);
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
    public FacultyTreeNode getParentNode() {
        return VancouverFaculties.SCIE;
    }

    @Override
    public FacultyTreeNode[] getChildren() {
        return childrenClass == null
                ? new FacultyTreeNode[0]
                : childrenClass.getEnumConstants()
                ;
    }

    @Override
    public Map<String, Course> getCourseIdTokenToCourseMap() {
        return courseCodeMap;
    }

}
