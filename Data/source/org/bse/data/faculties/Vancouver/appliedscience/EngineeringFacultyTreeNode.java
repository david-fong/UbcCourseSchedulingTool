package org.bse.data.faculties.Vancouver.appliedscience;

import org.bse.core.registration.FacultyTreeNodeIf;

public enum EngineeringFacultyTreeNode implements FacultyTreeNodeIf {
    BMEG("Biomedical"), // Should be "School of"
    CHML("Chemical and Biological"),
    CIVL("Civil"),
    CPEN("Computer"),
    ELEC("Electrical"),
    ENPH("Engineering Physics", ""),
    ENVE("Environmental"),
    GEOE("Geological"),
    IGEN("Integrated"),
    MECH("Mechanical"),
    MINE("Mining"), // Should be "Keevil Institute of"
    MTRL("Materials"),
    ;
    private final String name;

    EngineeringFacultyTreeNode(String name) {
        this.name = name + "";
    }
    EngineeringFacultyTreeNode(String name, String s) {
        this.name = name;
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
        return FacultyTreeNodeType.DEPARTMENT;
    }

    @Override
    public FacultyTreeNodeIf getParentNode() {
        return AppliedScienceFaculties._ENGINEERING_;
    }

    @Override
    public FacultyTreeNodeIf[] getChildren() {
        return null;
    }
}
