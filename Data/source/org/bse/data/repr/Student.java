package org.bse.data.repr;

import org.bse.data.repr.courseutils.Course;
import org.bse.data.repr.courseutils.CourseUtils.YearOfStudy;
import org.bse.data.repr.faculties.FacultyTreeRootCampus;
import org.bse.data.schedule.Schedule;
import org.bse.data.schedule.WorklistGroup;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO [doc]: write documentation.
 */
public final class Student implements XmlUtils.UserDataXml {

    private final String firstName;
    private final String lastName;
    private YearOfStudy currentYear;
    private FacultyTreeRootCampus.UbcCampuses campus;

    /*
    TODO [repr][Student]: Unless we keep data for sections from past years, this
     will need to change to map to a set of [Course]s instead of a [Schedule].
     Also, we need to investigate whether registering in any courses depend on
     whether the student got a certain mark for a prerequisite. This may require
     a wrapper class around [Course] that takes a [grade] constructor argument.
     I'd do it, but I'd feel bad about asking about this information from the user.
     */
    private final Map<YearOfStudy, Schedule> previousSchedules;
    private final Map<YearOfStudy, WorklistGroup> worklistGroups; // Worklists must not have the same name.

    // For first time-creation. Subsequent constructions
    // upon application-start will be from saved xml data.
    public Student(String firstName, String lastName, YearOfStudy yearOfStudy,
                   FacultyTreeRootCampus.UbcCampuses campus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentYear = yearOfStudy;
        this.campus = campus;
        this.previousSchedules = new EnumMap<>(YearOfStudy.class);
        this.worklistGroups = new EnumMap<>(YearOfStudy.class);
    }

    // TODO [xml:read][Student]
    // read cached student data.
    public Student(final Element studentElement) throws MalformedXmlDataException {
        this.firstName = XmlUtils.getMandatoryAttr(studentElement, Xml.FIRST_NAME_ATTR).getValue();
        this.lastName = XmlUtils.getMandatoryAttr(studentElement, Xml.LAST_NAME_ATTR).getValue();
        this.currentYear = YearOfStudy.decodeXmlAttr(XmlUtils.getMandatoryAttr(studentElement, Xml.YEAR_OF_STUDY_ATTR));
        this.campus = null; // need to create enum.static decoder

        this.previousSchedules = Collections.unmodifiableMap(new EnumMap<>(YearOfStudy.class)); // need to populate.
        this.worklistGroups = Collections.unmodifiableMap(new EnumMap<>(YearOfStudy.class)); // need to populate.
    }

    // TODO [xml:write][Student]
    @Override
    public Element toXml(final Document document) {
        final Element studentElement = document.createElement(Xml.STUDENT_TAG.value);
        studentElement.setAttribute(Xml.FIRST_NAME_ATTR.value, firstName);
        studentElement.setAttribute(Xml.LAST_NAME_ATTR.value, lastName);
        studentElement.setAttribute(Xml.YEAR_OF_STUDY_ATTR.value, currentYear.getXmlConstantValue());
        studentElement.setAttribute(Xml.CAMPUS_ATTR.value, campus.getXmlConstantValue());
        // previous schedules
        // worklists
        return studentElement;
    }

    public final String getFirstName() {
        return firstName;
    }

    public final String getLastName() {
        return lastName;
    }

    public final YearOfStudy getCurrentYear() {
        return currentYear;
    }

    public final FacultyTreeRootCampus.UbcCampuses getCampus() {
        return campus;
    }

    public final Map<YearOfStudy, Schedule> getPreviousSchedules() {
        return previousSchedules;
    }

    public final Map<YearOfStudy, WorklistGroup> getWorklistGroups() {
        return worklistGroups;
    }

    public final void setCurrentYear(YearOfStudy yearOfStudy) {
        this.currentYear = yearOfStudy;
    }

    public final void setCampus(FacultyTreeRootCampus.UbcCampuses campus) {
        this.campus = campus;
    }

    public final Set<Course> getCompletedCourses() {
        return null; // TODO:
    }



    // TODO [xml:spec][Student]
    public enum Xml implements XmlUtils.XmlConstant {
        STUDENT_TAG ("Student"),
        FIRST_NAME_ATTR ("firstName"),
        LAST_NAME_ATTR ("lastName"),
        YEAR_OF_STUDY_ATTR ("currentYear"),
        CAMPUS_ATTR ("campus"),
        PREVIOUS_COURSES_TAG ("PreviousCourses"),
        WORKLISTS_TAG ("Worklists"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
