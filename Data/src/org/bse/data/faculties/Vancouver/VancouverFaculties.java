package org.bse.data.faculties.Vancouver;

import org.bse.core.registration.FacultyTreeNodeIf;
import org.bse.data.faculties.Vancouver.appliedscience.AppliedScienceFaculties;

import static org.bse.core.registration.FacultyTreeNodeIf.FacultyTreeNodeType.*;

/**
 * https://www.ubc.ca/our-campuses/vancouver/directories/faculties-schools.html
 */
public enum VancouverFaculties implements FacultyTreeNodeIf {
    APSC (FACULTY, "Applied Science", AppliedScienceFaculties.class),
    // TODO:
    //
    //
    //
    //
    //
    //
    //
    //
    //
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
    public FacultyTreeNodeIf getParentNode() {
        return null;
    }

    @Override
    public FacultyTreeNodeIf[] getChildren() {
        return childrenClass.getEnumConstants();
    }
}
