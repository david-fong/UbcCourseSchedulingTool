package com.dvf.ucst.data;

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

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
    private final Map<YearOfStudy, Map<Semester, Set<Course>>> completedCoursesMap; // modifiable! be careful.
    private final Map<YearOfStudy, WorklistGroup> worklistGroups; // modifiable! be careful.

    // For first time-creation. Subsequent constructions
    // upon application-start will be from saved xml data.
    public Student(String firstName, String lastName, YearOfStudy yearOfStudy,
                   FacultyTreeRootCampus.UbcCampuses campus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentYear = yearOfStudy;
        this.campus = campus;
        this.completedCoursesMap = new EnumMap<>(YearOfStudy.class);
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

        this.completedCoursesMap = parseOutCompletedCoursesMap(
                XmlUtils.getMandatoryUniqueChildByTag(studentElement, Xml.PREVIOUS_COURSES_TAG)
        );

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

    public final Map<YearOfStudy, Map<Semester, Set<Course>>> getCompletedCoursesMap() {
        return completedCoursesMap;
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
        return completedCoursesMap.values().stream()
                .map(map -> map.values().stream())
                .flatMap(Function.identity())
                .reduce(new HashSet<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                })
                ; // TODO [impl:refine] find ways to cache this information.
    }

    // TODO [xml:read][Student.completedCoursesMap]
    private static Map<YearOfStudy, Map<Semester, Set<Course>>> parseOutCompletedCoursesMap(final Element coursesMapElement) {
        return new EnumMap<>(YearOfStudy.class);
    }

    @Override
    public Element toXml(final Document document) {
        final Element studentElement = document.createElement(Xml.STUDENT_TAG.value);
        studentElement.setAttribute(Xml.FIRST_NAME_ATTR.value, firstName);
        studentElement.setAttribute(Xml.LAST_NAME_ATTR.value, lastName);
        studentElement.setAttribute(Xml.YEAR_OF_STUDY_ATTR.value, currentYear.getXmlConstantValue());
        studentElement.setAttribute(Xml.CAMPUS_ATTR.value, campus.getXmlConstantValue());

        // TODO [xml:write][Student.previousSchedules]
        // previous schedules

        final Element worklistGroupsElement = document.createElement(Xml.WORKLIST_GROUPS_TAG.value);
        for (YearOfStudy yearOfStudy : worklistGroups.keySet()){
            final Element worklistGroupElement = worklistGroups.get(yearOfStudy).toXml(document);
            worklistGroupElement.setAttribute(Xml.YEAR_OF_STUDY_ATTR.value, yearOfStudy.getXmlConstantValue());
            worklistGroupsElement.appendChild(worklistGroupElement);
        }
        return studentElement;
    }



    public enum Xml implements XmlUtils.XmlConstant {
        STUDENT_TAG ("Student"),
        FIRST_NAME_ATTR ("firstName"),
        LAST_NAME_ATTR ("lastName"),
        YEAR_OF_STUDY_ATTR ("yearOfStudy"),
        CAMPUS_ATTR ("campus"),
        PREVIOUS_COURSES_TAG ("PreviousCourses"),
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

}
