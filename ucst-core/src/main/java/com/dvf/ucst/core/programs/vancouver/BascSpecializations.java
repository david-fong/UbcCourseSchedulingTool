package com.dvf.ucst.core.programs.vancouver;

import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.faculties.vancouver.BascFaculties;
import com.dvf.ucst.core.programs.ProgramOfStudy;
import com.dvf.ucst.core.programs.ProgramSpecialization;

import java.util.Map;
import java.util.Set;

/**
 *
 * https://courses.students.ubc.ca/cs/courseschedule?pname=specprogram&tname=specprogram&dept=BASC
 */
final class BascSpecializations {

    private static final ProgramOfStudy SHARED_PARENT_PROGRAM = VancouverProgramsOfStudy.BASC;
    private static final Map<FacultyTreeNode, Set<ProgramSpecialization>> SPECIALIZATIONS_MAP;
    static {
        SPECIALIZATIONS_MAP = Map.ofEntries(
                Map.entry(CpenSpecializations.SUBJECT, Set.of(CpenSpecializations.values())),
                Map.entry(ElecSpecializations.SUBJECT, Set.of(ElecSpecializations.values()))
        );
    }



    private interface BascProgramSpecialization extends ProgramSpecialization {
        @Override
        default ProgramOfStudy getParentProgram() {
            return SHARED_PARENT_PROGRAM;
        }
    }

    private enum CpenSpecializations implements BascProgramSpecialization {
        ;
        private static final FacultyTreeNode SUBJECT = BascFaculties.CPEN;

        @Override
        public FacultyTreeNode getSubject() {
            return SUBJECT;
        }
    }

    private enum ElecSpecializations implements BascProgramSpecialization {
        ;
        private static final FacultyTreeNode SUBJECT = BascFaculties.ELEC;

        @Override
        public FacultyTreeNode getSubject() {
            return SUBJECT;
        }
    }

}
