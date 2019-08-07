package org.bse.data.repr.faculties.vancouver;

import org.bse.data.repr.courseutils.Course;
import org.bse.data.repr.faculties.FacultyTreeNode;

import java.util.HashMap;
import java.util.Map;

import static org.bse.data.repr.faculties.FacultyTreeNode.FacultyTreeNodeType.DEPARTMENT;

public enum ScienceFaculties implements FacultyTreeNode {
    // TODO [dat]: add entries as needed. prefer alphabetical order.
    // ...
    CPSC (DEPARTMENT, "Computer Science"),
    // ...
    MATH (DEPARTMENT, "Mathematics"),
    // ...
    PHYS (DEPARTMENT, "Physics"),
    // ...
    ;
    private final FacultyTreeNodeType type;
    private final String name;
    private final Class<? extends FacultyTreeNode> childrenClass;
    private final Map<String, Course> courseCodeMap;

    <T extends Enum & FacultyTreeNode> ScienceFaculties
            (FacultyTreeNodeType type, String name, Class<T> childrenClass) {
        this.type = type;
        this.name = name;
        this.childrenClass = childrenClass;
        this.courseCodeMap = new HashMap<>();
    }

    ScienceFaculties(FacultyTreeNodeType type, String name) {
        this(type, name, null);
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
        return VancouverFaculties.SCIE;
    }

    @Override
    public FacultyTreeNode[] getChildren() {
        return childrenClass == null ? null : childrenClass.getEnumConstants();
    }

    @Override
    public Map<String, Course> getCodeStringToCourseMap() {
        return courseCodeMap;
    }

}
