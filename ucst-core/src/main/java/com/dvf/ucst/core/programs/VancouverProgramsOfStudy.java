package com.dvf.ucst.core.programs;

import com.dvf.ucst.core.faculties.FacultyTreeRootCampus;
import com.dvf.ucst.core.programs.specializations.ProgramSpecialization;

import java.util.Collections;
import java.util.List;

import static com.dvf.ucst.core.programs.ProgramOfStudy.ProgramOfStudyType.*;

/**
 *
 */
public enum VancouverProgramsOfStudy implements ProgramOfStudy {
    BA     ("", BACHELOR, null),
    BASC   ("", BACHELOR, null),
    BASMEN ("", BACHELOR, null),
    BCOM   ("", BACHELOR, null),
    BCS    ("", BACHELOR, null),
    BEDE   ("", BACHELOR, null),
    BEDM   ("", BACHELOR, null),
    BEDS   ("", BACHELOR, null),
    BEEM   ("", BACHELOR, null),
    BEND   ("", BACHELOR, null),
    BFA    ("", BACHELOR, null),
    BHE    ("", BACHELOR, null),
    BHK    ("", BACHELOR, null),
    BIE    ("", BACHELOR, null),
    BKIN   ("", BACHELOR, null),
    BMS    ("", BACHELOR, null),
    BSAB   ("", BACHELOR, null),
    BSAG   ("", BACHELOR, null),
    BSC    ("", BACHELOR, null),
    BSCA   ("", BACHELOR, null),
    BSCN   ("", BACHELOR, null),
    BSCW   ("", BACHELOR, null),
    BSF    ("", BACHELOR, null),
    BSFF   ("", BACHELOR, null),
    BSFN   ("", BACHELOR, null),
    BSFS   ("", BACHELOR, null),
    BSGR   ("", BACHELOR, null),
    BSN    ("", BACHELOR, null),
    BUF    ("", BACHELOR, null),
    CADS   ("", BACHELOR, null),
    DEDU   ("", BACHELOR, null),
    DMA    ("", BACHELOR, null),
    DPDT   ("", DIPLOMA,  null),
    DULE   ("", DIPLOMA,  null),
    EDD    ("", DOCTOR,   null),
    EMBA   ("", BACHELOR, null),
    JDMBA  ("", BACHELOR, null),
    LLBMBA ("", BACHELOR, null),
    MA     ("", MASTER,   null),
    MAS    ("", MASTER,   null),
    MASC   ("", MASTER,   null),
    MASLIS ("", MASTER,   null),
    MBA    ("", MASTER,   null),
    MBAMAA ("", MASTER,   null),
    MED    ("", MASTER,   null),
    MEL    ("", MASTER,   null),
    MENG   ("", MASTER,   null),
    MFA    ("", MASTER,   null),
    MHLP   ("", MASTER,   null),
    MLIS   ("", MASTER,   null),
    MM     ("", MASTER,   null),
    MMUS   ("", MASTER,   null),
    MSC    ("", MASTER,   null),
    MSCB   ("", MASTER,   null),
    MSN    ("", MASTER,   null),
    PHD    ("", DOCTOR,   null),
    ;
    private final String abbreviation;
    private final String nameNoTitle;
    private final ProgramOfStudyType programOfStudyType;
    private final List<ProgramSpecialization> specializations;

    <T extends Enum<T> & ProgramSpecialization> VancouverProgramsOfStudy(
            final String nameNoTitle,
            final ProgramOfStudyType programOfStudyType,
            final Class<T> specializationClass
    ) {
        this.abbreviation = name();
        this.nameNoTitle = nameNoTitle;
        this.programOfStudyType = programOfStudyType;
        this.specializations = (specializationClass == null)
                ? Collections.emptyList() // TODO [data:add][ProgramSpecialization] resolve all nulls in the far future.
                : List.of(specializationClass.getEnumConstants());
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public String getNameNoTitle() {
        return nameNoTitle;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public ProgramOfStudyType getType() {
        return programOfStudyType;
    }

    @Override
    public FacultyTreeRootCampus getCampusContext() {
        return FacultyTreeRootCampus.UbcCampuses.VANCOUVER;
    }

    @Override
    public List<ProgramSpecialization> getSpecializations() {
        return specializations;
    }

}
