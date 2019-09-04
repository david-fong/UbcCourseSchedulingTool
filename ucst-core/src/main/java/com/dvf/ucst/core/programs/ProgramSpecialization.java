package com.dvf.ucst.core.programs;

import com.dvf.ucst.core.faculties.FacultyTreeNode;

import java.nio.file.Path;

/**
 *
 */
public interface ProgramSpecialization {

    ProgramOfStudy getParentProgram();

    FacultyTreeNode getSubject();

    /**
     * TODO: uncomment and implement. make corresponding method for ProgramOfStudy.
     * Path to xml file mapping YearOfStudy to course requirements
     */
    //Path getCourseRequirementsPath();

}
