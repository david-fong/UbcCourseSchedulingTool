package com.dvf.ucst.core.programs;

import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.programs.ProgramOfStudy;

/**
 *
 */
public interface ProgramSpecialization {

    ProgramOfStudy getParentProgram();

    FacultyTreeNode getSubject();

}
