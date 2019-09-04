package com.dvf.ucst.core.programs;

import com.dvf.ucst.core.faculties.FacultyTreeRootCampus;
import com.dvf.ucst.core.programs.specializations.ProgramSpecialization;

import java.util.List;

/**
 *
 */
public interface ProgramOfStudy {

    String getAbbreviation();

    String getNameNoTitle();

    String getFullName();

    ProgramOfStudyType getType();

    FacultyTreeRootCampus getCampusContext();

    List<ProgramSpecialization> getSpecializations(); // must be unmodifiable list.



    /**
     *
     */
    enum ProgramOfStudyType {
        DIPLOMA ("Diploma", "in"),
        BACHELOR ("Bachelor", "of"),
        MASTER ("Master", "of"),
        DOCTOR ("Doctor", "of"),
        ;
        final String titleName;
        final String particle;

        ProgramOfStudyType(final String titleName, final String particle) {
            this.titleName = titleName;
            this.particle = particle;
        }

        String prependedTo(final ProgramOfStudy programOfStudy) {
            return String.format("%s %s %s", titleName, particle, programOfStudy.getNameNoTitle());
        }
    }

}
