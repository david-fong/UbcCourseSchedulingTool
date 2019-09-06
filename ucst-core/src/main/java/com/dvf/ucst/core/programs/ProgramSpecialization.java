package com.dvf.ucst.core.programs;

import com.dvf.ucst.core.UbcLocalFiles;
import com.dvf.ucst.core.faculties.FacultyTreeNode;

import java.nio.file.Path;

/**
 *
 */
public interface ProgramSpecialization extends UbcLocalFiles {

    ProgramOfStudy getParentProgram();

    FacultyTreeNode getSubject();

    default int getSpecializationId() {
        return -1; // TODO: implement for each enum.
    }

    @Override
    default Path getLocalDataPath() {
        return getParentProgram().getLocalDataPath()
                .resolve(getSubject().getAbbreviation())
                .resolve(Integer.toString(getSpecializationId()));
    }

    /**
     * TODO: uncomment and implement. make corresponding method for ProgramOfStudy.
     * Path to xml file mapping YearOfStudy to course requirements
     */
    //Path getCourseRequirementsPath();

}
