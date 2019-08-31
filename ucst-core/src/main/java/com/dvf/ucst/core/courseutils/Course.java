package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.HyperlinkBookIf;
import com.dvf.ucst.core.SectionIdString;
import com.dvf.ucst.core.Student;
import com.dvf.ucst.core.faculties.CampusNotFoundException;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.faculties.FacultyTreeRootCampus;
import com.dvf.ucst.core.schedule.Schedule;
import com.dvf.ucst.utils.pickybuild.PickyBuildElement;
import com.dvf.ucst.utils.requirement.Requirement;
import com.dvf.ucst.utils.requirement.matching.CreditValued;
import com.dvf.ucst.utils.requirement.matching.MatchingRequirementIf;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.util.*;

/**
 *
 */
public final class Course implements CreditValued, HyperlinkBookIf, SectionIdString {

    private static final String LAB_SECTION_ID_TOKEN_PREFIX = "L";
    private static final String TUTORIAL_SECTION_ID_TOKEN_PREFIX = "T";

    private final FacultyTreeNode facultyTreeNode;
    private final String courseIdToken;
    private final int creditValue;
    private final String descriptionString;

    // reqs are non-null:
    private final Requirement<Student> studentReqs; // TODO: go look at real requirement examples and see what attributes they include.
    private final MatchingRequirementIf<Schedule> prerequisites;
    private final MatchingRequirementIf<Schedule> corequisites;

    private final Set<CourseLectureSection> lectureSections;
    private final Set<CourseSection> labSections;
    private final Set<CourseSection> tutorialSections;

    public Course(final Element courseElement) throws MalformedXmlDataException {
        try { // get faculty node:
            this.facultyTreeNode = FacultyTreeRootCampus.UbcCampuses.getCampusByIdToken(
                    XmlUtils.getMandatoryAttr(courseElement, Xml.COURSE_CAMPUS_ATTR).getValue()
            ).getSquashedFacultyAbbrMap().get(
                    XmlUtils.getMandatoryAttr(courseElement, Xml.COURSE_FACULTY_ATTR).getValue()
            );
        } catch (CampusNotFoundException e) {
            throw new MalformedXmlDataException(e);
        }

        this.descriptionString = XmlUtils.getMandatoryUniqueChildByTag(
                courseElement, Xml.DESCRIPTION_TAG).getTextContent();

        this.creditValue = Integer.parseInt(XmlUtils.getMandatoryAttr(
                courseElement, Xml.COURSE_CREDIT_ATTR).getValue());

        this.courseIdToken = XmlUtils.getMandatoryAttr(
                courseElement, Xml.COURSE_CODE_ATTR).getValue();

        assert creditValue >= 0 : "credit value must be equal to or greater than zero";

        // TODO [xml:read][Course:reqs]
        this.studentReqs = new Requirement.StrictlyFailingReq<>();
        this.prerequisites = new MatchingRequirementIf.StrictlyFailingMatchThreshReq<>();
        this.corequisites = new MatchingRequirementIf.StrictlyFailingMatchThreshReq<>();

        this.labSections = parseOutLabTutorialSections(courseElement, Xml.LABS_TAG);
        this.tutorialSections = parseOutLabTutorialSections(courseElement, Xml.TUTORIALS_TAG);

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

    public final String getCourseIdToken() {
        return courseIdToken;
    }

    @Override
    public final int getCreditValue() {
        return creditValue;
    }

    public final String getCourseDescription() {
        return descriptionString;
    }

    @Override
    public final String getSystemFullSectionIdString() {
        return facultyTreeNode.getRootCampus().getAbbreviation()
                + " " + getUserFullSectionIdString();
    }

    @Override
    public final String getUserFullSectionIdString() {
        return facultyTreeNode.getAbbreviation()
                + " " + courseIdToken;
    }

    @Override
    public String toString() {
        return getSystemFullSectionIdString();
    }

    @Override
    public final String getRegistrationSiteUrl() {
        return RegistrationSubjAreaQuery.getCourseUrl(this);
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

    public final CourseSection getSectionByIdToken(final String sectionIdToken) throws CourseSectionNotFoundException {
        final Optional<CourseLectureSection> section;
        if (sectionIdToken.startsWith(LAB_SECTION_ID_TOKEN_PREFIX)) {
            return null;
        } else if (sectionIdToken.startsWith(TUTORIAL_SECTION_ID_TOKEN_PREFIX)) {
            return null;
        } else {
             section = lectureSections.stream()
                    .filter(lectureSec -> lectureSec.getSectionIdToken().equals(sectionIdToken))
                    .findAny();
        }
        if (section.isPresent()) {
            return section.get();
        } else {
            throw new CourseSectionNotFoundException(this, sectionIdToken);
        }
    }

    // helper for xml constructor.
    private Set<CourseSection> parseOutLabTutorialSections(final Element courseElement, final Xml sectionGroupTag) throws MalformedXmlDataException {
        final String idPrefix;
        switch (sectionGroupTag) {
            case LABS_TAG:      idPrefix = LAB_SECTION_ID_TOKEN_PREFIX; break;
            case TUTORIALS_TAG: idPrefix = TUTORIAL_SECTION_ID_TOKEN_PREFIX; break;
            default: throw new RuntimeException(new IllegalArgumentException());
        }

        final List<Element> SectionElements = XmlUtils.getChildElementsByTagName(
                XmlUtils.getMandatoryUniqueChildByTag(courseElement, Xml.LABS_TAG),
                SecXml.COURSE_SECTION_TAG
        );

        final Set<CourseSection> sectionGroup = new HashSet<>(SectionElements.size());
        for (final Element sectionElement : SectionElements) {
            final CourseSection labSectionObj = new CourseSection(sectionElement);
            if (!labSectionObj.sectionIdToken.startsWith(idPrefix)) {
                throw new MalformedXmlDataException(String.format("section id tokens"
                        + " under the \"%s\" tag must start with a \"%s\"",
                        sectionGroupTag.getXmlConstantValue(), idPrefix
                ));
            }
            sectionGroup.add(labSectionObj);
        }
        return Collections.unmodifiableSet(sectionGroup);
    }

    // TODO [xml:write.setup][CourseWip]: Add static xml producer method taking [CourseWip]



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
    public class CourseSection implements PickyBuildElement<CourseSection>, HyperlinkBookIf, SectionIdString {

        private final String sectionIdToken;
        private final CourseUtils.Semester semester;
        private final Professor professor;
        private final Set<CourseSectionBlock> blocks;

        private CourseSection(final Element sectionElement) throws MalformedXmlDataException {
            this.sectionIdToken = XmlUtils.getMandatoryAttr(
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

        public final boolean overlapsWith(final CourseSection other) {
            // *the equality comparison is an optimization - not essential.
            return this.equals(other) || (
                    getSemester() == other.getSemester() && getBlocks().stream()
                            .anyMatch(block -> other.getBlocks().stream().anyMatch(block::overlapsWith)
                    )
            );
        }

        public final Course getParentCourse() {
            return Course.this;
        }

        public final String getSectionIdToken() {
            return sectionIdToken;
        }

        @Override
        public final String toString() {
            return Course.this.toString() + " " + getSectionIdToken();
        }

        @Override
        public Set<Set<CourseSection>> getPickyBuildFriends() {
            return Collections.emptySet();
        }

        @Override
        public String getRegistrationSiteUrl() {
            return RegistrationSubjAreaQuery.getSectionUrl(this);
        }

        @Override
        public final String getSystemFullSectionIdString() {
            return getParentCourse().getSystemFullSectionIdString()
                    + " " + getSectionIdToken();
        }

        @Override
        public final String getUserFullSectionIdString() {
            return getParentCourse().getUserFullSectionIdString()
                    + " " + getSectionIdToken();
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

        // TODO [xml:write.setup][CourseSectionWip]: Add static producer method taking [CourseSectionWip]
    }

    /**
     * Implementation note: must be constructed after lab and tutorial sections
     * to be able to refer to them.
     */
    public final class CourseLectureSection extends CourseSection {

        private final Set<CourseSection> requiredLabOptions;
        private final Set<CourseSection> requiredTutorialOptions;
        private final Set<Set<CourseSection>> pickyBuildFriends; // unmodifiable;

        private CourseLectureSection(final Element lectureElement) throws MalformedXmlDataException {
            super(lectureElement);

            this.requiredLabOptions = getComplimentarySectionOptionsFromElement(
                    XmlUtils.getOptionalUniqueChildByTag(lectureElement, Xml.LABS_TAG)
            );
            this.requiredTutorialOptions = getComplimentarySectionOptionsFromElement(
                    XmlUtils.getOptionalUniqueChildByTag(lectureElement, Xml.TUTORIALS_TAG)
            );
            this.pickyBuildFriends = Set.of(requiredLabOptions, requiredTutorialOptions);
        }

        // helper for xml constructor. Throws RuntimeException if a referenced lab/tutorial is not found.
        private Set<CourseSection> getComplimentarySectionOptionsFromElement(final Element optionsElement)
                throws MalformedXmlDataException {
            if (optionsElement == null) {
                return Collections.emptySet();
            } else {
                final Set<CourseSection> complementarySections = new HashSet<>();
                for (final Element sectionElement : XmlUtils.getChildElementsByTagName(
                        optionsElement, SecXml.COURSE_SECTION_TAG
                )) {
                    try {
                        complementarySections.add(getSectionByIdToken(XmlUtils.getMandatoryAttr(
                                sectionElement, SecXml.LECTURE_COMPLIMENTARY_SECTION_REF_ATTR
                        ).getValue()));
                    } catch (CourseSectionNotFoundException e) {
                        throw new RuntimeException("lab or tutorial sections for a lecture"
                                + " must be from the same course as the lecture", e);
                    }
                }
                return Collections.unmodifiableSet(complementarySections);
            }
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

        @Override
        public final Set<Set<CourseSection>> getPickyBuildFriends() {
            return pickyBuildFriends;
        }

        // TODO [xml:write.setup][CourseLectureSectionWip]: Add static producer method taking [CourseLectureSectionWip]
    }

    public static boolean isSectionIdTokenForLectureSection(final String sectionIdToken) {
        return !sectionIdToken.startsWith(LAB_SECTION_ID_TOKEN_PREFIX)
                && !sectionIdToken.startsWith(TUTORIAL_SECTION_ID_TOKEN_PREFIX);
        // return sectionIdToken.matches("^\\p{Digit}.*");
    }



    public enum Xml implements XmlUtils.XmlConstant {
        COURSE_TAG ("Course"),
        COURSE_CAMPUS_ATTR ("campus"),
        COURSE_FACULTY_ATTR ("faculty"),
        COURSE_CODE_ATTR ("code"),
        COURSE_CREDIT_ATTR ("credits"),
        DESCRIPTION_TAG ("Description"),

        STUDENT_REQS_TAG ("StudentReqs"),
        PREREQS_TAG ("Prerequisites"),
        COREQS_TAG ("Corequisites"),

        LECTURES_TAG ("Lectures"),
        /*
        the below two tags are optional for use inside a lecture section element.
        if present, list complimentary sections to the same course as the lecture
        of which a student must take one at the same time as the declaring lecture
        element to be considered taking the course. If not found, none are required.
         */
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
        LECTURE_COMPLIMENTARY_SECTION_REF_ATTR ("at"),
        SECTION_CODE_ATTR ("code"),
        SECTION_SEMESTER_ATTR ("semester"), // See [CourseUtils.Semester]
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
