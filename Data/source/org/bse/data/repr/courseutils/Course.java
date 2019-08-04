package org.bse.data.repr.courseutils;

import org.bse.data.repr.CourseSchedule;
import org.bse.data.repr.HyperlinkBookIf;
import org.bse.data.repr.Professor;
import org.bse.data.repr.Student;
import org.bse.data.repr.faculties.FacultyTreeNode;
import org.bse.utils.requirement.Requirement;
import org.bse.utils.requirement.operators.matching.CreditValued;
import org.bse.utils.requirement.operators.matching.MatchingRequirementIf;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class Course implements CreditValued, CodeStringPath, HyperlinkBookIf {

    private final FacultyTreeNode facultyTreeNode;
    private final int creditValue;
    private final String courseCodeToken;
    private final String descriptionString;
    private final String registrationUrlString;

    // non-null:
    private final Requirement<Student> studentReqs;
    private final MatchingRequirementIf<CourseSchedule> prerequisites;
    private final MatchingRequirementIf<CourseSchedule> corequisites;

    private final Set<CourseLectureSection> lectureSections;
    private final Set<CourseSection> labSections;
    private final Set<CourseSection> tutorialSections;

    // TODO [xml:read][Course]
    public Course(Document courseDocument) {
        final Element courseElement; {
            NodeList nodeList = courseDocument.getElementsByTagName(Xml.COURSE_TAG.value);
            courseElement = (Element)nodeList.item(0);
        }

        facultyTreeNode = null;
        descriptionString = null;
        creditValue = -1;
        courseCodeToken = null;
        registrationUrlString = null;

        prerequisites = null;
        corequisites = null;
        studentReqs = null;

        lectureSections = null;
        labSections = null;
        tutorialSections = null;
    }

    public final FacultyTreeNode getFacultyTreeNode() {
        return facultyTreeNode;
    }

    public final String getCourseDescription() {
        return descriptionString;
    }

    @Override
    public int getCreditValue() {
        return creditValue;
    }

    @Override
    public String getFullCodeString() {
        return facultyTreeNode.getAbbreviation() + " " + courseCodeToken;
    }

    @Override
    public String getRegistrationLink() {
        return registrationUrlString;
    }

    public final Requirement<Student> getStudentReqs() {
        return studentReqs;
    }

    public final MatchingRequirementIf<CourseSchedule> getPrerequisites() {
        return prerequisites;
    }

    public final MatchingRequirementIf<CourseSchedule> getCorequisites() {
        return corequisites;
    }

    public final Set<CourseLectureSection> getLectureSections() {
        return lectureSections;
    }

    public final Set<CourseSection> getLabSections() {
        return labSections;
    }

    public final Set<CourseSection> getTutorialSections() {
        return tutorialSections;
    }


    /**
     * Represents a section of a [Course]. On top of the properties provided by a
     * [Course], a [CourseSection] includes information on the lecturer, times and
     * places of meetings, and seating availability and restrictions. Seating
     * availability is not saved as part of state.
     *
     * All code tokens ([CodeStringPath]) for instances under a common [Course]
     * should be unique.
     *
     * TODO: add representation of seating / methods to fetch seating state from web.
     */
    public class CourseSection implements CodeStringPath {

        private final String sectionCode;
        private final CourseUtils.Semester semester;
        private final Professor professor;
        private final Set<CourseSectionBlock> blocks;

        // TODO [xml:read][CourseSection]
        private CourseSection(Element sectionElement) {
            sectionCode = null;
            semester = null;
            professor = null; // See [Professor.fromXml]
            blocks = new HashSet<>(); // TODO: make unmodifiable
        }

        public final boolean overlapsWith(CourseSection other) {
            return semester == other.semester && blocks.stream().anyMatch(block ->
                    other.blocks.stream().anyMatch(block::overlapsWith)
            );
        }

        public final Course getParentCourse() {
            return Course.this;
        }

        public final String getFullCodeString() {
            return Course.this.getFullCodeString() + sectionCode;
        }

        public final CourseUtils.Semester getSemester() {
            return semester;
        }

        public final Professor getProfessor() {
            return professor;
        }

        public final Set<CourseSectionBlock> getBlocks() {
            return blocks;
        }
    }

    /**
     *
     */
    public final class CourseLectureSection extends CourseSection {

        private final Set<CourseSection> requiredLabOptions;
        private final Set<CourseSection> requiredTutorialOptions;

        // TODO [xml:read][CourseLectureSection]
        private CourseLectureSection(Element lectureElement) {
            super(lectureElement);

            // TODO: make unmodifiable
            this.requiredLabOptions = new HashSet<>();
            this.requiredTutorialOptions = new HashSet<>();
        }

        /**
         * @return A non-empty set of [CourseSection]s for labs from which a student
         *     must register for ONE to be considered taking the [Course] returned by
         *     [getParentCourse], or null if that [Course] has no labs.
         */
        public Set<CourseSection> getRequiredLabOptions() {
            return requiredLabOptions;
        }

        /**
         * @return A non-empty set of [CourseSection]s for tutorials from which a student
         *     must register for ONE to be considered taking the [Course] returned by
         *     [getParentCourse], or null if that [Course] has no tutorials.
         */
        public Set<CourseSection> getRequiredTutorialOptions() {
            return requiredTutorialOptions;
        }
    }



    public enum Xml {
        COURSE_TAG ("Course"),
        COURSE_FACULTY_ATTR ("faculty"),
        COURSE_CODE_ATTR ("code"),
        COURSE_CREDIT_ATTR ("credits"),
        DESCRIPTION_TAG ("Description"),

        STUDENT_REQS_TAG ("StudentReqs"),
        PREREQS_TAG ("Prerequisites"),
        COREQS_TAG ("Corequisites"),

        LECTURES_TAG ("Lectures"),
        LABS_TAG ("Labs"),
        TUTORIALS_TAG ("Tutorials"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }
    }

    public enum SecXml {
        COURSE_SECTION_TAG ("Section"),
        SECTION_CODE_ATTR ("code"),
        SECTION_SEMESTER_ATTR ("semester"),
        SECTION_PROFESSOR ("Instructor"),
        ;
        private final String value;

        SecXml(String value) {
            this.value = value;
        }
    }

}
