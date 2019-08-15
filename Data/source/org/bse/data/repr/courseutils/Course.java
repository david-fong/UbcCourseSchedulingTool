package org.bse.data.repr.courseutils;

import org.bse.data.repr.HyperlinkBookIf;
import org.bse.data.repr.Professor;
import org.bse.data.repr.Student;
import org.bse.data.repr.faculties.FacultyTreeNode;
import org.bse.data.schedule.Schedule;
import org.bse.utils.requirement.Requirement;
import org.bse.utils.requirement.operators.matching.CreditValued;
import org.bse.utils.requirement.operators.matching.MatchingRequirementIf;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public final class Course implements CreditValued, HyperlinkBookIf {

    private final FacultyTreeNode facultyTreeNode;
    private final int creditValue;
    private final String courseCodeToken;
    private final String descriptionString;
    private final String registrationUrlString;

    // reqs are non-null:
    private final Requirement<Student> studentReqs;
    private final MatchingRequirementIf<Schedule> prerequisites;
    private final MatchingRequirementIf<Schedule> corequisites;

    private final Set<CourseLectureSection> lectureSections;
    private final Set<CourseSection> labSections;
    private final Set<CourseSection> tutorialSections;

    public Course(final Document courseDocument) throws MalformedXmlDataException {
        final Element courseElement; {
            NodeList nodeList = courseDocument.getElementsByTagName(Xml.COURSE_TAG.value);
            courseElement = (Element)nodeList.item(0);
        }

        this.facultyTreeNode = null; // TODO [xml:read][Course:facultyNode]

        this.descriptionString = XmlUtils.getMandatoryUniqueChildByTag(
                courseElement, Xml.DESCRIPTION_TAG
        ).getTextContent();
        this.creditValue = Integer.parseInt(XmlUtils.getMandatoryAttr(
                courseElement, Xml.COURSE_CREDIT_ATTR
        ).getValue());
        this.courseCodeToken = XmlUtils.getMandatoryAttr(
                courseElement, Xml.COURSE_CODE_ATTR
        ).getValue();
        this.registrationUrlString = facultyTreeNode.getRegistrationSiteUrl()
                .replace(QuerySpecifierTokens.FACULTY.tnameQueryVal,
                        QuerySpecifierTokens.COURSE.tnameQueryVal) // TODO [style] is there a nicer, more reusable way to do this?
                + QuerySpecifierTokens.COURSE.tokenStub
                + courseCodeToken;

        assert creditValue >= 0 : "credit value must be equal to or greater than zero";

        // TODO [xml:read][Course:reqs]
        this.studentReqs = new Requirement.StrictlyFailingReq<>();
        this.prerequisites = new MatchingRequirementIf.StrictlyFailingMatchThreshReq<>();
        this.corequisites = new MatchingRequirementIf.StrictlyFailingMatchThreshReq<>();

        // lab sections:
        final List<Element> labSectionElements = XmlUtils.getChildElementsByTagName(
                XmlUtils.getMandatoryUniqueChildByTag(courseElement, Xml.LABS_TAG),
                SecXml.COURSE_SECTION_TAG
        );
        final Set<CourseLectureSection> labSections = new HashSet<>(labSectionElements.size());
        for (Element sectionElement : labSectionElements) {
            labSections.add(new CourseLectureSection(sectionElement));
        }
        this.labSections = Collections.unmodifiableSet(labSections);

        // tutorial sections:
        final List<Element> tutorialSectionElements = XmlUtils.getChildElementsByTagName(
                XmlUtils.getMandatoryUniqueChildByTag(courseElement, Xml.TUTORIALS_TAG),
                SecXml.COURSE_SECTION_TAG
        );
        final Set<CourseLectureSection> tutorialSections = new HashSet<>(tutorialSectionElements.size());
        for (Element sectionElement : tutorialSectionElements) {
            tutorialSections.add(new CourseLectureSection(sectionElement));
        }
        this.tutorialSections = Collections.unmodifiableSet(tutorialSections);

        // lecture sections (must be done last to refer to labs and tutorials):
        final List<Element> lectureSectionElements = XmlUtils.getChildElementsByTagName(
                XmlUtils.getMandatoryUniqueChildByTag(courseElement, Xml.LECTURES_TAG),
                SecXml.COURSE_SECTION_TAG
        );
        final Set<CourseLectureSection> lectureSections = new HashSet<>(lectureSectionElements.size());
        for (Element sectionElement : lectureSectionElements) {
            lectureSections.add(new CourseLectureSection(sectionElement));
        }
        this.lectureSections = Collections.unmodifiableSet(lectureSections);
    }

    public final FacultyTreeNode getFacultyTreeNode() {
        return facultyTreeNode;
    }

    public final String getCourseDescription() {
        return descriptionString;
    }

    @Override
    public final int getCreditValue() {
        return creditValue;
    }

    @Override
    public String toString() {
        return facultyTreeNode.getAbbreviation() + " " + courseCodeToken;
    }

    @Override
    public String getRegistrationSiteUrl() {
        return registrationUrlString;
    }

    public final Requirement<Student> getStudentReqs() {
        return studentReqs;
    }

    public final MatchingRequirementIf<Schedule> getPrerequisites() {
        return prerequisites;
    }

    public final MatchingRequirementIf<Schedule> getCorequisites() {
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
     * All code tokens for [CourseSection]s under a common [Course] should be unique.
     *
     * TODO [rep]: add representation of seating / methods to fetch seating state from web.
     */
    public class CourseSection implements CourseSectionRef, HyperlinkBookIf {

        private final String sectionCode;
        private final CourseUtils.Semester semester;
        private final Professor professor;
        private final Set<CourseSectionBlock> blocks;

        private CourseSection(final Element sectionElement) throws MalformedXmlDataException {
            this.sectionCode = XmlUtils.getMandatoryAttr(
                    sectionElement, SecXml.SECTION_CODE_ATTR
            ).getValue();
            this.semester = CourseUtils.Semester.decodeXmlAttr(XmlUtils.getMandatoryAttr(
                    sectionElement, SecXml.SECTION_SEMESTER_ATTR
            ));
            this.professor = new Professor(XmlUtils.getMandatoryUniqueChildByTag(
                    sectionElement, SecXml.SECTION_PROFESSOR_TAG
            ));

            final List<Element> blockElements = XmlUtils.getChildElementsByTagName(
                    sectionElement, CourseSectionBlock.Xml.BLOCK_TAG
            );
            final Set<CourseSectionBlock> blocks = new HashSet<>(blockElements.size());
            for (Element blockElement : blockElements) {
                blocks.add(new CourseSectionBlock(blockElement));
            }
            this.blocks = Collections.unmodifiableSet(blocks);
        }

        public final boolean overlapsWith(CourseSection other) {
            return semester == other.semester && blocks.stream().anyMatch(block ->
                    other.blocks.stream().anyMatch(block::overlapsWith)
            );
        }

        public final Course getParentCourse() {
            return Course.this;
        }

        @Override
        public final String toString() {
            return Course.this.toString() + " " + sectionCode;
        }

        @Override
        public final boolean isLoaded() {
            return true;
        }

        @Override
        public String getRegistrationSiteUrl() {
            return Course.this.getRegistrationSiteUrl()
                    .replace(QuerySpecifierTokens.COURSE.tnameQueryVal,
                            QuerySpecifierTokens.SECTION.tnameQueryVal)
                    + QuerySpecifierTokens.SECTION.tokenStub
                    + sectionCode;
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

        @Override
        public final CourseSection dereference() {
            return this;
        }
    }

    /**
     * Implementation note: must be constructed after lab and tutorial sections
     * to be able to refer to them.
     */
    public final class CourseLectureSection extends CourseSection {

        private final Set<CourseSection> requiredLabOptions;
        private final Set<CourseSection> requiredTutorialOptions;

        // TODO [xml:read][CourseLectureSection]
        private CourseLectureSection(final Element lectureElement) throws MalformedXmlDataException {
            super(lectureElement);

            // TODO: make unmodifiable:
            this.requiredLabOptions = new HashSet<>();
            this.requiredTutorialOptions = new HashSet<>();
        }

        /**
         * @return A non-empty set of [CourseSection]s for labs from which a student
         *     must register for ONE to be considered taking the [Course] returned by
         *     [getParentCourse], or null if that [Course] has no labs.
         */
        public final Set<CourseSection> getRequiredLabOptions() {
            return requiredLabOptions;
        }

        /**
         * @return A non-empty set of [CourseSection]s for tutorials from which a student
         *     must register for ONE to be considered taking the [Course] returned by
         *     [getParentCourse], or null if that [Course] has no tutorials.
         */
        public final Set<CourseSection> getRequiredTutorialOptions() {
            return requiredTutorialOptions;
        }
    }



    public enum Xml implements XmlUtils.XmlConstant {
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

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

    public enum SecXml implements XmlUtils.XmlConstant {
        COURSE_SECTION_TAG ("Section"),
        SECTION_CODE_ATTR ("code"),
        SECTION_SEMESTER_ATTR ("semester"), // See [CourseUtils.Semester
        SECTION_PROFESSOR_TAG("Instructor"),
        ;
        private final String value;

        SecXml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
