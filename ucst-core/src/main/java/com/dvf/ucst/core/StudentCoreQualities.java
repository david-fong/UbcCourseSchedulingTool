package com.dvf.ucst.core;

import com.dvf.ucst.core.courseutils.CourseUtils.YearOfStudy;
import com.dvf.ucst.core.faculties.FacultyTreeRootCampus;
import com.dvf.ucst.core.programs.ProgramOfStudy;
import com.dvf.ucst.core.programs.ProgramSpecialization;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.util.function.Function;

/**
 * Bundles together the four core qualities of a student:
 * 0. Campus
 * 1. Program
 * 2. Year
 * 3. Specialization
 */
public final class StudentCoreQualities implements XmlUtils.UserDataXml {

    private YearOfStudy yearOfStudy;
    private ProgramSpecialization programSpecialization;

    // first time constructor:
    StudentCoreQualities(
            final YearOfStudy yearOfStudy,
            final ProgramSpecialization programSpecialization
    ) {
        this.yearOfStudy = yearOfStudy;
        this.programSpecialization = programSpecialization;
    }

    // xml constructor:
    StudentCoreQualities(final Element qualitiesElement) throws MalformedXmlDataException {
        this.yearOfStudy = YearOfStudy.decodeXmlAttr(XmlUtils.getMandatoryAttr(
                qualitiesElement, Xml.YEAR_OF_STUDY_ATTR
        ));
        this.programSpecialization = null; // TODO: how to get this?
    }

    public final FacultyTreeRootCampus.UbcCampuses getCampus() {
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

    @Override
    public Element toXml(final Function<XmlUtils.XmlConstant, Element> elementSupplier) {
        final Element qualitiesElement = elementSupplier.apply(Xml.CORE_QUALITIES_TAG);
        qualitiesElement.setAttribute(
                Xml.YEAR_OF_STUDY_ATTR.getXmlConstantValue(),
                getYearOfStudy().getXmlConstantValue()
        );
        qualitiesElement.setAttribute(
                Xml.CAMPUS_ATTR.getXmlConstantValue(),
                getCampus().getAbbreviation()
        );
        return qualitiesElement;
    }



    /**
     * TODO:
     */
    enum Xml implements XmlUtils.XmlConstant {
        CORE_QUALITIES_TAG ("CoreQualities"),
        YEAR_OF_STUDY_ATTR ("year"),
        CAMPUS_ATTR ("campus"),
        ;
        private final String value;

        Xml(final String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return null;
        }
    }

}
