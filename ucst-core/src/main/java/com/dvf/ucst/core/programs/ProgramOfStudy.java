package com.dvf.ucst.core.programs;

import com.dvf.ucst.core.UbcLocalFiles;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.faculties.UbcCampuses;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Directory structure:
 * campus >> programs >> specializations >> subject >> files named after specialization UID.
 */
public interface ProgramOfStudy extends UbcLocalFiles {

    String getAbbreviation();

    String getNameNoTitle();

    String getFullName();

    ProgramOfStudyType getType();

    UbcCampuses getCampusContext();

    @Override
    default Path getLocalDataPath() {
        return UbcLocalDataCategory.PROGRAMS.getRootDir()
                .resolve(getAbbreviation());
    }
    /*
    TODO [investigate]: It seems like StudentCoreQualityReqs don't care about the
     actual specialization- and instead just about the subject (faculty/dept) of
     the specialization under a program of study. If so, it may not be necessary
     to implement the methods for specializations like their UID, name, and notes.
     Actually, we probably do- assuming each specialization under the same subject
     can have its own mappings of years of study to course requirements.
     */
    Map<FacultyTreeNode, Set<ProgramSpecialization>> getSpecializations(); // must be unmodifiable map.

    // TODO[spec]: ^design how reqs that need to finished before a certain year are represented.
    //  also, reqs will need to be able to refer to common reqs like the engineering "impact of tech
    //  on society" candidates, and the arts elective candidates. How to decide where to put and how
    //  to refer to them in a way that specifies that?

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
