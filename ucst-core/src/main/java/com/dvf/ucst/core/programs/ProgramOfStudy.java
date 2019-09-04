package com.dvf.ucst.core.programs;

import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.faculties.FacultyTreeRootCampus;

import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface ProgramOfStudy {

    String getAbbreviation();

    String getNameNoTitle();

    String getFullName();

    ProgramOfStudyType getType();

    FacultyTreeRootCampus getCampusContext();

    Map<FacultyTreeNode, Set<ProgramSpecialization>> getSpecializations(); // must be unmodifiable map.



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
