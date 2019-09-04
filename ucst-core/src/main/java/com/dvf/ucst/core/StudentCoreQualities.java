package com.dvf.ucst.core;

import com.dvf.ucst.core.courseutils.CourseUtils.YearOfStudy;
import com.dvf.ucst.core.faculties.FacultyTreeRootCampus;
import com.dvf.ucst.core.programs.ProgramOfStudy;
import com.dvf.ucst.core.programs.ProgramSpecialization;

/**
 * Bundles together the four core qualities of a student:
 * 0. Campus
 * 1. Program
 * 2. Year
 * 3. Specialization
 *
 */
public final class StudentCoreQualities {

    private YearOfStudy yearOfStudy;
    private ProgramSpecialization programSpecialization;

    StudentCoreQualities(
            final YearOfStudy yearOfStudy,
            final ProgramSpecialization programSpecialization
    ) {
        this.yearOfStudy = yearOfStudy;
        this.programSpecialization = programSpecialization;
    }

    public final FacultyTreeRootCampus getCampus() {
        return getProgramOfStudy().getCampusContext();
    }

    public final ProgramOfStudy getProgramOfStudy() {
        return getProgramSpecialization().getParentProgram();
    }

    public final YearOfStudy getYearOfStudy() {
        return yearOfStudy;
    }

    public final ProgramSpecialization getProgramSpecialization() {
        return programSpecialization;
    }

    /**
     * @param yearOfStudy Will be rejected if lower than the current value returned
     *     by [::getYearOfStudy].
     */
    void setYearOfStudy(final YearOfStudy yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    /**
     * @param programSpecialization A [ProgramSpecialization] to set. If this
     *     specialization is under a different program than the current program,
     *     the current program will be changed to match. The same goes for the
     *     specialization's program's campus.
     */
    void setProgramSpecialization(final ProgramSpecialization programSpecialization) {
        this.programSpecialization = programSpecialization;
    }

}
