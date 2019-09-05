package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.HyperlinkBookIf;
import com.dvf.ucst.core.SectionIdString;
import com.dvf.ucst.core.spider.CourseWip.CourseSectionWip;
import com.dvf.ucst.utils.general.WorkInProgress;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
public abstract class AbstractCourseSection implements
        HyperlinkBookIf,
        SectionIdString {

    private final String sectionIdToken;
    private final CourseUtils.Semester semester;
    private final Professor professor;
    private final boolean isWaitlist;
    private final Set<CourseSectionBlock> blocks;

    AbstractCourseSection(final Element sectionElement) throws MalformedXmlDataException {
        assert sectionElement.getTagName().equals(SecXml.COURSE_SECTION_TAG.getXmlConstantValue());
        this.sectionIdToken = XmlUtils.getMandatoryAttr(
                sectionElement, SecXml.SECTION_CODE_ATTR
        ).getValue();
        this.semester = CourseUtils.Semester.decodeXmlAttr(XmlUtils.getMandatoryAttr(
                sectionElement, SecXml.SECTION_SEMESTER_ATTR
        ));
        this.professor = new Professor(XmlUtils.getMandatoryUniqueChildByTag(
                sectionElement, SecXml.SECTION_PROFESSOR_TAG
        ));
        this.isWaitlist = sectionElement.hasAttribute(
                SecXml.OPTIONAL_WAITLIST_FLAG_ATTR.getXmlConstantValue()
        );

        // parse out [CourseSectionBlock]s.
        final List<Element> blockElements = XmlUtils.getChildElementsByTagName(
                sectionElement, CourseSectionBlock.Xml.BLOCK_TAG
        );
        final Set<CourseSectionBlock> blocks = new HashSet<>(blockElements.size());
        for (final Element blockElement : blockElements) {
            blocks.add(new CourseSectionBlock(blockElement));
        }
        try {
            CourseSectionBlock.InternalConflictException.checkForConflicts(blocks);
        } catch (final CourseSectionBlock.InternalConflictException e) {
            throw new MalformedXmlDataException(String.format("Sections were verified"
                    + " not to have scheduling conflicts during fetching from UBC's"
                    + " registration site. See %s::createXmlOfBlockWips. Perhaps the"
                    + " user tampered with the xml files.", CourseSectionBlock.class
            ), e);
        }
        this.blocks = Collections.unmodifiableSet(blocks);
    }

    public final boolean overlapsWith(final AbstractCourseSection other) {
        // *the equality comparison is an optimization - not essential.
        return this.equals(other) || (
                getSemester() == other.getSemester() && getBlocks().stream()
                        .anyMatch(block -> other.getBlocks().stream().anyMatch(block::overlapsWith)
                        )
        );
    }

    public abstract Course getParentCourse();

    public final String getSectionIdToken() {
        return sectionIdToken;
    }

    @Override
    public final String toString() {
        return getParentCourse().toString() + " " + getSectionIdToken();
    }

    @Override
    public abstract String getRegistrationSiteUrl();

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

    public final boolean isWaitlist() {
        return isWaitlist;
    }

    public final Set<CourseSectionBlock> getBlocks() {
        return blocks;
    }

    // TODO [xml:write.setup][CourseSectionWip]: Add static producer method taking [CourseSectionWip]
    private static Element createXmlOfSectionWip(
            final Function<XmlUtils.XmlConstant, Element> elementSupplier,
            final CourseSectionWip wip
    ) throws WorkInProgress.IncompleteWipException, CourseSectionBlock.IllegalTimeEnclosureException {
        final Element sectionElement = elementSupplier.apply(SecXml.COURSE_SECTION_TAG);
        sectionElement.setAttribute(
                SecXml.SECTION_CODE_ATTR.getXmlConstantValue(),
                wip.getSectionIdToken()
        );
        sectionElement.setAttribute(
                SecXml.SECTION_SEMESTER_ATTR.getXmlConstantValue(),
                wip.getSemester().getXmlConstantValue()
        ); {
            // TODO [xml:write][Course]: professor element
        }
        if (wip.isWaitlist()) {
            sectionElement.setAttribute(SecXml.OPTIONAL_WAITLIST_FLAG_ATTR.getXmlConstantValue(), "");
        } {
            for (final CourseSectionWip.CourseSectionBlockWip blockWip : wip.getBlocks()) {
                sectionElement.appendChild(CourseSectionBlock.createXmlOfWorkInProgress(elementSupplier, blockWip));
            }
        }
        return sectionElement;
    }



    /**
     *
     */
    public enum SecXml implements XmlUtils.XmlConstant {
        COURSE_SECTION_TAG ("Section"),
        LECTURE_COMPLIMENTARY_SECTION_REF_ATTR ("at"),
        SECTION_CODE_ATTR ("code"),
        SECTION_SEMESTER_ATTR ("semester"), // See [CourseUtils.Semester]
        SECTION_PROFESSOR_TAG("Instructor"),
        OPTIONAL_WAITLIST_FLAG_ATTR ("waitlist"), // if this attribute exists, the section is a waitlist.
        // note: Blocks are not grouped under an element. That is why there is no tag for such a grouping element.
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
