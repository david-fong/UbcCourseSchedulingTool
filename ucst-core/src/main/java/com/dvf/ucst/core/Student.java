package com.dvf.ucst.core;

import com.dvf.ucst.core.faculties.FacultyCourseNotFoundException;
import com.dvf.ucst.core.faculties.UbcCampuses;
import com.dvf.ucst.core.programs.ProgramSpecialization;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.courseutils.CourseUtils.Semester;
import com.dvf.ucst.core.courseutils.CourseUtils.YearOfStudy;
import com.dvf.ucst.core.faculties.CampusNotFoundException;
import com.dvf.ucst.core.schedule.WorklistGroup;
import org.w3c.dom.Element;

import java.util.*;
import java.util.function.Function;

/**
 * A central collection of user data.
 */
public final class Student implements XmlUtils.UserDataXml {

    private final String firstName;
    private final String lastName;
    private final StudentCoreQualities coreQualities;

    private final Set<CompletedCourse> completedCourses; // modifiable! be careful.
    private final Set<CompletedCourse> publicCompleteCourses; // unmodifiable.
    private final Map<YearOfStudy, WorklistGroup> worklistGroups; // modifiable! be careful.

    // For first time-creation. Subsequent constructions
    // upon application-start will be from saved xml data.
    public Student(String firstName, String lastName, StudentCoreQualities coreQualities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.coreQualities = coreQualities;
        this.completedCourses = new HashSet<>();
        this.publicCompleteCourses = Collections.unmodifiableSet(completedCourses);
        this.worklistGroups = new EnumMap<>(YearOfStudy.class);
    }

    // read cached student data.
    public Student(final Element studentElement) throws MalformedXmlDataException {
        this.firstName = XmlUtils.getMandatoryAttr(studentElement, Xml.FIRST_NAME_ATTR).getValue();
        this.lastName = XmlUtils.getMandatoryAttr(studentElement, Xml.LAST_NAME_ATTR).getValue();
        this.coreQualities = new StudentCoreQualities(XmlUtils.getMandatoryUniqueChildByTag(
                studentElement,
                StudentCoreQualities.Xml.CORE_QUALITIES_TAG
        ));

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

    public final StudentCoreQualities getCoreQualities() {
        return coreQualities;
    }

    public final Map<YearOfStudy, WorklistGroup> getWorklistGroups() {
        return worklistGroups;
    }

    public final void setCurrentYear(final YearOfStudy yearOfStudy) {
        getCoreQualities().setYearOfStudy(yearOfStudy);
    }

    void setProgramSpecialization(final ProgramSpecialization programSpecialization) {
        getCoreQualities().setProgramSpecialization(programSpecialization);
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
    public Element toXml(final Function<XmlUtils.XmlConstant, Element> elementSupplier) {
        final Element studentElement = elementSupplier.apply(Xml.STUDENT_TAG);
        studentElement.setAttribute(Xml.FIRST_NAME_ATTR.getXmlConstantValue(), firstName);
        studentElement.setAttribute(Xml.LAST_NAME_ATTR.getXmlConstantValue(), lastName);

        // core qualities:
        studentElement.appendChild(getCoreQualities().toXml(elementSupplier));

        // previous schedules:
        final Element completedCoursesElement = elementSupplier.apply(Xml.COMPLETED_COURSES_TAG);
        for (final CompletedCourse completedCourse : publicCompleteCourses) {
            completedCoursesElement.appendChild(completedCourse.toXml(elementSupplier));
        }

        // current worklists:
        final Element worklistGroupsElement = elementSupplier.apply(Xml.WORKLIST_GROUPS_TAG);
        for (final YearOfStudy yearOfStudy : getWorklistGroups().keySet()){
            final Element worklistGroupElement = getWorklistGroups().get(yearOfStudy).toXml(elementSupplier);
            worklistGroupElement.setAttribute(
                    Xml.YEAR_OF_STUDY_ATTR.getXmlConstantValue(),
                    yearOfStudy.getXmlConstantValue()
            );
            worklistGroupsElement.appendChild(worklistGroupElement);
        }
        return studentElement;
    }



    /**
     * Wrapper around a [Course].
     * TODO [repr][Student]: investigate whether registering in any courses depend on
     *      whether the student got a certain mark for a prerequisite. This may require
     *      a wrapper class around [Course] that takes a [grade] constructor argument.
     *      I'd do it, but I'd feel bad about asking about this information from the user.
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
                this.completedCourse = UbcCampuses
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
        public Element toXml(final Function<XmlUtils.XmlConstant, Element> elementSupplier) {
            final Element completedElement = elementSupplier.apply(CompletedCourseXml.COMPLETED_COURSE_TAG);
            completedElement.setAttribute(
                    CompletedCourseXml.YEAR_ATTR.getXmlConstantValue(),
                    yearCompleted.getXmlConstantValue()
            );
            completedElement.setAttribute(
                    CompletedCourseXml.SEMESTER_ATTR.getXmlConstantValue(),
                    semesterCompleted.getXmlConstantValue()
            );
            completedElement.setAttribute(
                    CompletedCourseXml.COURSE_ID_ATTR.getXmlConstantValue(),
                    completedCourse.getSystemFullSectionIdString()
            );
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
