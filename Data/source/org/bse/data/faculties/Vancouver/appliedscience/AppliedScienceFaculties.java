package org.bse.data.faculties.Vancouver.appliedscience;

import org.bse.core.registration.FacultyTreeNodeIf;
import org.bse.data.faculties.Vancouver.VancouverFaculties;

import static org.bse.core.registration.FacultyTreeNodeIf.FacultyTreeNodeType.*;

public enum AppliedScienceFaculties implements FacultyTreeNodeIf {
    // TODO: add entries as needed. prefer alphabetical order.
    // ...
    _ENGINEERING_(FACULTY, "Engineering", EngineeringFacultyTreeNode.class)
    // ...
    ;
    private final FacultyTreeNodeType type;
    private final String name;
    private final Class<? extends FacultyTreeNodeIf> childrenClass;

    <T extends Enum & FacultyTreeNodeIf> AppliedScienceFaculties
            (FacultyTreeNodeType type, String name, Class<T> childrenClass) {
        this.type = type;
        this.name = name;
        this.childrenClass = childrenClass;
    }

    AppliedScienceFaculties(FacultyTreeNodeType type, String name) {
        this(type, name, null);
    }

    @Override
    public String getNameNoTitle() {
        return name;
    }

    // TODO: Not sure what to do about the non-4-character names...
    @Override
    public String getAbbreviation() {
        return name();
    }

    @Override
    public FacultyTreeNodeType getType() {
        return type;
    }

    @Override
    public FacultyTreeNodeIf getParentNode() {
        return VancouverFaculties.APSC;
    }

    @Override
    public FacultyTreeNodeIf[] getChildren() {
        return childrenClass == null ? null : childrenClass.getEnumConstants();
    }

}
