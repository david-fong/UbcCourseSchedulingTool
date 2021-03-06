package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.HyperlinkBookIf;
import com.dvf.ucst.core.SectionIdString;
import com.dvf.ucst.core.Student.CompletedCourse;
import com.dvf.ucst.core.StudentCoreQualities;
import com.dvf.ucst.core.UbcLocalFiles;
import com.dvf.ucst.core.courseutils.categorymatchers.CourseCategory;
import com.dvf.ucst.core.faculties.CampusNotFoundException;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.faculties.UbcCampuses;
import com.dvf.ucst.core.spider.CourseWip;
import com.dvf.ucst.core.spider.CourseWip.CourseSectionWip;
import com.dvf.ucst.utils.general.WorkInProgress.IncompleteWipException;
import com.dvf.ucst.utils.pickybuild.PickyBuildElement;
import com.dvf.ucst.utils.requirement.Requirement;
import com.dvf.ucst.utils.requirement.matching.CreditValued;
import com.dvf.ucst.utils.requirement.matching.MatchingRequirementIf;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlIoUtils;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

/**
 *
 */
public final class Course implements CreditValued, HyperlinkBookIf, SectionIdString, UbcLocalFiles {

    private static final String LAB_SECTION_ID_TOKEN_PREFIX = "L";
    private static final String TUTORIAL_SECTION_ID_TOKEN_PREFIX = "T";

    private final FacultyTreeNode facultyTreeNode;
    private final String courseIdToken;
    private final int creditValue;
    private final String descriptionString;
    private final Set<CourseCategory> categories =
            Collections.unmodifiableSet(EnumSet.noneOf(CourseCategory.class)); // unmodifiable. // TODO

    // reqs are non-null:
    private final Requirement<StudentCoreQualities> studentReqs;
    private final MatchingRequirementIf<Set<CompletedCourse>> prerequisites;
    private final MatchingRequirementIf<Set<CompletedCourse>> corequisites;

    private final Set<CourseLectureSection> lectureSections;
    private final Set<CourseSection> labSections;
    private final Set<CourseSection> tutorialSections;

    public Course(final Element courseElement) throws MalformedXmlDataException {
        assert courseElement.getTagName().equals(Xml.COURSE_TAG.getXmlConstantValue());
        try { // get faculty node:
            this.facultyTreeNode = UbcCampuses.getCampusByIdToken(
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

        this.labSections = parseOutLabTutorialSections(
                XmlUtils.getMandatoryUniqueChildByTag(courseElement, Xml.LABS_TAG),
                LAB_SECTION_ID_TOKEN_PREFIX
        );
        this.tutorialSections = parseOutLabTutorialSections(
                XmlUtils.getMandatoryUniqueChildByTag(courseElement, Xml.TUTORIALS_TAG),
                TUTORIAL_SECTION_ID_TOKEN_PREFIX
        );
        // lecture sections (must be done after loading labs and tutorials to refer to them as objects):
        final List<Element> lectureSectionElements = XmlUtils.getChildElementsByTagName(
                XmlUtils.getMandatoryUniqueChildByTag(courseElement, Xml.LECTURES_TAG),
                AbstractCourseSection.SecXml.COURSE_SECTION_TAG
        );
        final Set<CourseLectureSection> lectureSections = new HashSet<>(lectureSectionElements.size());
        for (final Element sectionElement : lectureSectionElements) {
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

    public Set<CourseCategory> getCategories() {
        return categories;
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

    @Override
    public Path getLocalDataPath() {
        return getLocalDataPath(getFacultyTreeNode(), getCourseIdToken());
    }
    public static Path getLocalDataPath(final FacultyTreeNode facultyNode, final String courseIdToken) {
        return facultyNode.getLocalDataPath()
                .resolve("courses") // TODO: replace string literal with a constant string
                .resolve(courseIdToken + XmlIoUtils.XML_EXTENSION_STRING);
    }

    public final Requirement<StudentCoreQualities> getStudentReqs() {
        return studentReqs;
    }

    public final MatchingRequirementIf<Set<CompletedCourse>> getPrerequisites() {
        return prerequisites;
    }

    public final MatchingRequirementIf<Set<CompletedCourse>> getCorequisites() {
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
        final Set<? extends CourseSection> sectionGroup;
        if (sectionIdToken.startsWith(LAB_SECTION_ID_TOKEN_PREFIX)) {
            sectionGroup = getLabSections();
        } else if (sectionIdToken.startsWith(TUTORIAL_SECTION_ID_TOKEN_PREFIX)) {
            sectionGroup = getTutorialSections();
        } else {
             sectionGroup = getLectureSections();
        }
        // filter for the section with the same id:
        final Optional<? extends CourseSection> section = sectionGroup.stream()
                .filter(sec -> sec.getSectionIdToken().equals(sectionIdToken))
                .findAny();
        if (section.isPresent()) {
            return section.get();
        } else {
            throw new CourseSectionNotFoundException(this, sectionIdToken);
        }
    }

    // helper for xml constructor.
    private Set<CourseSection> parseOutLabTutorialSections(
            final Element sectionGroupElement,
            final String idPrefix
    ) throws MalformedXmlDataException {
        final List<Element> SectionElements = XmlUtils.getChildElementsByTagName(
                sectionGroupElement,
                AbstractCourseSection.SecXml.COURSE_SECTION_TAG
        );
        final Set<CourseSection> sectionGroup = new HashSet<>(SectionElements.size());
        for (final Element sectionElement : SectionElements) {
            // construct the lab/tutorial section object:
            final CourseSection sectionObject = new CourseSection(sectionElement);
            if (!sectionObject.getSectionIdToken().startsWith(idPrefix)) {
                throw new MalformedXmlDataException(String.format("section id tokens"
                        + " under the \"%s\" tag must start with a \"%s\"",
                        sectionGroupElement.getTagName(), idPrefix
                ));
            }
            sectionGroup.add(sectionObject);
        }
        return Collections.unmodifiableSet(sectionGroup);
    }

    /**
     * @param elementSupplier Supplier of [Element]s which can be added to the
     *     [Document] that the returned [Element] will ultimately be added to.
     * @param wip The [CourseWip] describing the properties to populate [xml] with.
     * @return An [Element] that can be used for this class' constructor.
     * @throws IncompleteWipException if [wip] is not complete.
     */
    public static Element createXmlOfWorkInProgress(
            final Function<XmlUtils.XmlConstant, Element> elementSupplier,
            final CourseWip wip
    ) throws IncompleteWipException {
        final Element courseElement = elementSupplier.apply(Xml.COURSE_TAG);
        courseElement.setAttribute(
                Xml.COURSE_CAMPUS_ATTR.getXmlConstantValue(),
                wip.getFacultyTreeNode().getRootCampus().getAbbreviation()
        );
        courseElement.setAttribute(
                Xml.COURSE_FACULTY_ATTR.getXmlConstantValue(),
                wip.getFacultyTreeNode().getAbbreviation()
        );
        courseElement.setAttribute(
                Xml.COURSE_CODE_ATTR.getXmlConstantValue(),
                wip.getCourseIdToken()
        );
        courseElement.setAttribute(
                Xml.COURSE_CREDIT_ATTR.getXmlConstantValue(),
                Integer.toString(wip.getCreditValue())
        ); {
            final Element descriptionElement = elementSupplier.apply(Xml.DESCRIPTION_TAG);
            descriptionElement.setTextContent(wip.getDescriptionString());
            courseElement.appendChild(descriptionElement);
        } {
            // TODO [xml:write][Course requirements]
        } {
            // lectures, labs, tutorials
        }
        return courseElement;
    }



    /**
     *
     */
    public class CourseSection extends AbstractCourseSection implements PickyBuildElement<CourseSection> {

        private CourseSection(final Element sectionElement) throws MalformedXmlDataException {
            super(sectionElement);
        }

        @Override
        public Course getParentCourse() {
            return Course.this;
        }

        @Override
        public String getRegistrationSiteUrl() {
            return RegistrationSubjAreaQuery.getSectionUrl(this);
        }

        @Override
        public Set<Set<CourseSection>> getPickyBuildFriends() {
            return Collections.emptySet();
        }
    }

    /**
     * Implementation note: must be constructed after lab and tutorial sections
     * to be able to refer to them.
     */
    public class CourseLectureSection extends CourseSection {

        private final Set<CourseSection> requiredLabOptions;
        private final Set<CourseSection> requiredTutorialOptions;
        private final Set<Set<CourseSection>> pickyBuildFriends; // unmodifiable;

        private CourseLectureSection(final Element lectureElement) throws MalformedXmlDataException {
            super(lectureElement);

            this.requiredLabOptions = getComplimentarySectionOptionsFromElement(
                    XmlUtils.getOptionalUniqueChildByTag(lectureElement, Course.Xml.LABS_TAG)
            );
            this.requiredTutorialOptions = getComplimentarySectionOptionsFromElement(
                    XmlUtils.getOptionalUniqueChildByTag(lectureElement, Course.Xml.TUTORIALS_TAG)
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
    }

    // TODO [xml:write.setup][CourseLectureSectionWip]: Add static producer method taking [CourseLectureSectionWip]
    private static Element createXmlOfWorkInProgress(
            final Function<XmlUtils.XmlConstant, Element> elementSupplier,
            final CourseSectionWip.CourseLectureSectionWip wip
    ) throws IncompleteWipException {
        final Element lectureElement = elementSupplier.apply(AbstractCourseSection.SecXml.COURSE_SECTION_TAG);

        return lectureElement;
    }

    public static boolean isSectionIdTokenForLectureSection(final String sectionIdToken) {
        return !sectionIdToken.startsWith(LAB_SECTION_ID_TOKEN_PREFIX)
                && !sectionIdToken.startsWith(TUTORIAL_SECTION_ID_TOKEN_PREFIX);
        // return sectionIdToken.matches("^\\p{Digit}.*");
    }



    /**
     *
     */
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
        the below two tags list complimentary sections to the same course as the lecture
        of which a student must take one at the same time as the declaring lecture
        element to be considered taking the course. If empty, then none are required.
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

}
