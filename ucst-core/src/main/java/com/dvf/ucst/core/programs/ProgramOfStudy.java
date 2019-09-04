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

    FacultyTreeRootCampus.UbcCampuses getCampusContext();

    /*
    TODO [investigate]: It seems like StudentCoreQualityReqs don't care about the
     actual specialization- and instead just about the subject (faculty/dept) of
     the specialization under a program of study. If so, it may not be necessary
     to implement the methods for specializations like their UID, name, and notes.
     actually, we probably do- assuming each specialization under the same subject
     can have its own mappings of years of study to course requirements.
     */
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
