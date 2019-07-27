package org.bse.data.faculties.vancouver;

import org.bse.data.faculties.FacultyTreeNodeIf;

public enum AppliedScienceFaculties implements FacultyTreeNodeIf {
    BMEG("Biomedical"),
    CHML("Chemical and Biological"),
    CIVL("Civil"),
    CPEN("Computer"),
    ELEC("Electrical"),
    ENPH("Engineering Physics", ""),
    ENVE("Environmental"),
    GEOE("Geological"),
    IGEN("Integrated"),
    MECH("Mechanical"),
    MINE("Mining"),
    MTRL("Materials"),
    ;
    private final String name;

    AppliedScienceFaculties(String name) {
        this.name = name + " Engineering";
    }
    AppliedScienceFaculties(String name, String s) {
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
        return VancouverFaculties.APSC;
    }

    @Override
    public FacultyTreeNodeIf[] getChildren() {
        return null;
    }
}
