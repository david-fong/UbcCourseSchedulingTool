package com.dvf.ucst.data;

import com.dvf.ucst.data.faculties.FacultyCourseNotFoundException;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import com.dvf.ucst.data.courseutils.Course;
import com.dvf.ucst.data.courseutils.CourseUtils.Semester;
import com.dvf.ucst.data.courseutils.CourseUtils.YearOfStudy;
import com.dvf.ucst.data.faculties.CampusNotFoundException;
import com.dvf.ucst.data.faculties.FacultyTreeRootCampus;
import com.dvf.ucst.data.schedule.WorklistGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * A central collection of user data.
 */
public final class Student implements XmlUtils.UserDataXml {

    private final String firstName;
    private final String lastName;
    private YearOfStudy currentYear;
    private FacultyTreeRootCampus.UbcCampuses campus;

    /*
    TODO [repr][Student]: investigate whether registering in any courses depend on
     whether the student got a certain mark for a prerequisite. This may require
     a wrapper class around [Course] that takes a [grade] constructor argument.
     I'd do it, but I'd feel bad about asking about this information from the user.
     */
    private final Set<CompletedCourse> completedCourses; // modifiable! be careful.
    private final Set<CompletedCourse> publicCompleteCourses; // unmodifiable.
    private final Map<YearOfStudy, WorklistGroup> worklistGroups; // modifiable! be careful.

    // For first time-creation. Subsequent constructions
    // upon application-start will be from saved xml data.
    public Student(String firstName, String lastName, YearOfStudy yearOfStudy,
                   FacultyTreeRootCampus.UbcCampuses campus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentYear = yearOfStudy;
        this.campus = campus;
        this.completedCourses = new HashSet<>();
        this.publicCompleteCourses = Collections.unmodifiableSet(completedCourses);
        this.worklistGroups = new EnumMap<>(YearOfStudy.class);
    }

    // read cached student data.
    public Student(final Element studentElement) throws MalformedXmlDataException {
        this.firstName = XmlUtils.getMandatoryAttr(studentElement, Xml.FIRST_NAME_ATTR).getValue();
        this.lastName = XmlUtils.getMandatoryAttr(studentElement, Xml.LAST_NAME_ATTR).getValue();
        this.currentYear = YearOfStudy.decodeXmlAttr(XmlUtils.getMandatoryAttr(studentElement, Xml.YEAR_OF_STUDY_ATTR));
        try {
            this.campus = FacultyTreeRootCampus.UbcCampuses.getCampusByIdToken(
                    XmlUtils.getMandatoryAttr(studentElement, Xml.CAMPUS_ATTR).getValue()
            );
        } catch (CampusNotFoundException e) {
            throw new MalformedXmlDataException(e);
        }

        this.completedCourses = parseOutCompletedCourses(
                XmlUtils.getMandatoryUniqueChildByTag(studentElement, Xml.COMPLETED_COURSES_TAG)
        );
        this.publicCompleteCourses = Collections.unmodifiableSet(completedCourses);

        // parse out [WorklistGroup] objects:
        final Map<YearOfStudy, WorklistGroup> worklistGroups = new EnumMap<>(YearOfStudy.class);
        final List<Element> worklistGroupElements = XmlUtils.getChildElementsByTagName(
                XmlUtils.getMandatoryUniqueChildByTag(studentElement, Xml.WORKLIST_GROUPS_TAG),
                WorklistGroup.Xml.WORKLIST_GROUP_TAG
        );
        for (Element worklistGroupElement : worklistGroupElements) {
            final YearOfStudy WorklistGroupYear = YearOfStudy.decodeXmlAttr(
                    XmlUtils.getMandatoryAttr(worklistGroupElement, Xml.YEAR_OF_STUDY_ATTR)
            );
            worklistGroups.put(WorklistGroupYear, new WorklistGroup(worklistGroupElement));
        }
        this.worklistGroups = worklistGroups;
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

    public final Map<YearOfStudy, WorklistGroup> getWorklistGroups() {
        return worklistGroups;
    }

    public final void setCurrentYear(YearOfStudy yearOfStudy) {
        this.currentYear = yearOfStudy;
    }

    public final void setCampus(FacultyTreeRootCampus.UbcCampuses campus) {
        this.campus = campus;
    }

    public final Set<CompletedCourse> getCompletedCourses() {
        return publicCompleteCourses;
    }

    private static Set<CompletedCourse> parseOutCompletedCourses(final Element completedCoursesElement) throws MalformedXmlDataException {
        final List<Element> completedElements = XmlUtils.getChildElementsByTagName(
                completedCoursesElement, CompletedCourseXml.COMPLETED_COURSE_TAG
        );
        final Set<CompletedCourse> completedCourses = new HashSet<>(completedElements.size());
        for (final Element completedElement : completedElements) {
            completedCourses.add(new CompletedCourse(completedElement));
        }
        return completedCourses;
    }

    @Override
    public Element toXml(final Document document) {
        final Element studentElement = document.createElement(Xml.STUDENT_TAG.value);
        studentElement.setAttribute(Xml.FIRST_NAME_ATTR.value, firstName);
        studentElement.setAttribute(Xml.LAST_NAME_ATTR.value, lastName);
        studentElement.setAttribute(Xml.YEAR_OF_STUDY_ATTR.value, currentYear.getXmlConstantValue());
        studentElement.setAttribute(Xml.CAMPUS_ATTR.value, campus.getXmlConstantValue());

        // previous schedules
        final Element completedCoursesElement = document.createElement(Xml.COMPLETED_COURSES_TAG.value);
        for (final CompletedCourse completedCourse : publicCompleteCourses) {
            completedCoursesElement.appendChild(completedCourse.toXml(document));
        }

        final Element worklistGroupsElement = document.createElement(Xml.WORKLIST_GROUPS_TAG.value);
        for (YearOfStudy yearOfStudy : worklistGroups.keySet()){
            final Element worklistGroupElement = worklistGroups.get(yearOfStudy).toXml(document);
            worklistGroupElement.setAttribute(Xml.YEAR_OF_STUDY_ATTR.value, yearOfStudy.getXmlConstantValue());
            worklistGroupsElement.appendChild(worklistGroupElement);
        }
        return studentElement;
    }


    /**
     * Wrapper around a [Course].
     */
    public static final class CompletedCourse implements XmlUtils.UserDataXml {

        private final YearOfStudy yearCompleted;
        private final Semester semesterCompleted;
        private final Course completedCourse;

        private CompletedCourse(final Element completedElement) throws MalformedXmlDataException {
            this.yearCompleted = YearOfStudy.decodeXmlAttr(XmlUtils.getMandatoryAttr(
                    completedElement, CompletedCourseXml.YEAR_ATTR
            ));
            this.semesterCompleted = Semester.decodeXmlAttr(XmlUtils.getMandatoryAttr(
                    completedElement, CompletedCourseXml.SEMESTER_ATTR
            ));
            final String[] courseIdTokens = XmlUtils.getMandatoryAttr(
                    completedElement, CompletedCourseXml.COURSE_ID_ATTR
            ).getValue().split("\\s+");
            try {
                this.completedCourse = FacultyTreeRootCampus.UbcCampuses
                        .getCampusByIdToken(courseIdTokens[0])
                        .getSquashedFacultyAbbrMap().get(courseIdTokens[1])
                        .getCourseByCodeString(courseIdTokens[2]);
            } catch (CampusNotFoundException | FacultyCourseNotFoundException e) {
                throw new MalformedXmlDataException(e);
            }
        }

        public final YearOfStudy getYearCompleted() {
            return yearCompleted;
        }

        public final Semester getSemesterCompleted() {
            return semesterCompleted;
        }

        public final Course getCourse() {
            return completedCourse;
        }

        @Override
        public Element toXml(Document document) {
            final Element completedElement = document.createElement(CompletedCourseXml.COMPLETED_COURSE_TAG.value);
            completedElement.setAttribute(CompletedCourseXml.YEAR_ATTR.value, yearCompleted.getXmlConstantValue());
            completedElement.setAttribute(CompletedCourseXml.SEMESTER_ATTR.value, semesterCompleted.getXmlConstantValue());
            completedElement.setAttribute(CompletedCourseXml.COURSE_ID_ATTR.value, completedCourse.getSystemFullSectionIdString());
            return completedElement;
        }
    }

    public enum Xml implements XmlUtils.XmlConstant {
        STUDENT_TAG ("Student"),
        FIRST_NAME_ATTR ("firstName"),
        LAST_NAME_ATTR ("lastName"),
        YEAR_OF_STUDY_ATTR ("yearOfStudy"),
        CAMPUS_ATTR ("campus"),
        COMPLETED_COURSES_TAG("CompletedCourses"),
        WORKLIST_GROUPS_TAG("Worklists"),
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

    private enum CompletedCourseXml implements XmlUtils.XmlConstant {
        COMPLETED_COURSE_TAG ("Completed"),
        COURSE_ID_ATTR("courseId"), // follows "full-system" format (includes campus specifier).
        YEAR_ATTR("year"),
        SEMESTER_ATTR("semester"),
        ;
        private final String value;

        CompletedCourseXml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
