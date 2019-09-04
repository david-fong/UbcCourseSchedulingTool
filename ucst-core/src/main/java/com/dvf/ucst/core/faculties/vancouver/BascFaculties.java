package com.dvf.ucst.core.faculties.vancouver;

import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.faculties.FacultyTreeNode;

import java.util.HashMap;
import java.util.Map;

public enum BascFaculties implements FacultyTreeNode {
    BMEG ("Biomedical"),
    CHML ("Chemical and Biological"),
    CIVL ("Civil"),
    CPEN ("Computer"),
    ELEC ("Electrical"),
    ENPH ("Engineering Physics", ""),
    ENVE ("Environmental"),
    GEOE ("Geological"),
    IGEN ("Integrated"),
    MANU ("Manufacturing"),
    MECH ("Mechanical"),
    MINE ("Mining"),
    MTRL ("Materials"),
    ;
    private final String name;
    private final Map<String, Course> courseCodeMap;

    BascFaculties(String name) {
        this.name = name + " Engineering";
        this.courseCodeMap = new HashMap<>();
    }
    BascFaculties(String name, String s) {
        this.name = name;
        this.courseCodeMap = new HashMap<>();
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
    public FacultyTreeNodeType getFacultyType() {
        return FacultyTreeNodeType.DEPARTMENT;
    }

    @Override
    public FacultyTreeNode getParentNode() {
        return VancouverFaculties.APSC;
    }

    @Override
    public FacultyTreeNode[] getChildren() {
        return new FacultyTreeNode[0];
    }

    @Override
    public Map<String, Course> getCourseIdTokenToCourseMap() {
        return courseCodeMap;
    }

}
