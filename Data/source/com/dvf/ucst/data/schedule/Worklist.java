package com.dvf.ucst.data.schedule;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import com.dvf.ucst.data.courseutils.Course;
import com.dvf.ucst.data.courseutils.Course.CourseSection;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;

/**
 * A mutable wrapper for a [Schedule] object. While this class
 * can be used for generating schedules in the [PickyBuildGenerator], it
 * is not intended for such use, which should be left to [ScheduleBuild].
 */
public final class Worklist extends ScheduleBuild implements XmlUtils.UserDataXml {

    private final String name;
    private boolean isLocked = false;
    private WorklistFavorability favorability = WorklistFavorability.NEUTRAL;
    // ^does not effect internal behaviour.

    // Up-cast / copy constructor:
    Worklist(final ScheduleBuild otherSchedule, final String name) {
        super(otherSchedule);
        this.name = name;

        if (otherSchedule instanceof Worklist) {
            final Worklist otherWorklist = (Worklist)otherSchedule;
            this.isLocked = otherWorklist.isLocked;
            this.favorability = otherWorklist.favorability;
        }
    }

    // Reconstruct from saved user-data:
    Worklist(final Element worklistElement) throws MalformedXmlDataException {
        super(worklistElement);
        this.name = XmlUtils.getMandatoryAttr(worklistElement, Xml.WORKLIST_NAME_ATTR).getValue();
        this.isLocked = worklistElement.getAttribute(Xml.WORKLIST_IS_LOCKED_ATTR.value) != null;
        this.favorability = WorklistFavorability.decodeXmlAttr(
                XmlUtils.getMandatoryAttr(
                        worklistElement,
                        Xml.WORKLIST_FAVORABILITY_ATTR
                )
        );
    }

    // For users to create from scratch:
    Worklist(final String name) {
        super();
        this.name = name;
    }

    @Override
    public Worklist copy() {
        return new Worklist(this, name);
    }

    @Override
    public boolean addIfNoConflicts(CourseSection section) {
        // short-circuits the operation (skips call to super) if locked.
        return !isLocked && super.addIfNoConflicts(section);
    }

    /**
     * @param section A [CourseSection] to attempt to remove from this [Worklist].
     *     This operation will fail if [section] is a [CourseSection] in this
     *     [Worklist]'s STT sections.
     * @return [true] if the operation was successful and [false] otherwise.
     */
    public final boolean removeSection(CourseSection section) {
        return !isLocked && !getEnclosedSttSections().contains(section) && courseSections.remove(section);
    }

    public final String getName() {
        return name;
    }

    public final boolean isLocked() {
        return isLocked;
    }

    public final WorklistFavorability getFavorability() {
        return favorability;
    }

    public final void setLocked(final boolean locked) {
        this.isLocked = locked;
    }

    public final void setFavorability(WorklistFavorability favorability) {
        this.favorability = favorability;
    }

    @Override
    public Element toXml(final Document document) {
        final Element worklistElement = document.createElement(Xml.WORKLIST_TAG.value);
        {
            // Add xml data for non-STT sections:
            final Set<CourseSection> nonSttSections = new HashSet<>(getCourseSections());
            nonSttSections.removeAll(getEnclosedSttSections());
            worklistElement.appendChild(createSectionListElement(
                    document, nonSttSections, Schedule.Xml.MANUAL_SECTION_LIST_TAG
            ));
            // Add xml data for STT sections:
            final Element sttSectionListElement = createSectionListElement(
                    document, getEnclosedSttSections(), Schedule.Xml.STT_SECTION_LIST_TAG
            );
            sttSectionListElement.setAttribute(Schedule.Xml.STT_NAME_ATTR.getXmlConstantValue(), getEnclosedSttName());
            worklistElement.appendChild(sttSectionListElement);
        }
        worklistElement.setAttribute(Xml.WORKLIST_NAME_ATTR.value, name);
        if (isLocked) {
            worklistElement.setAttribute(Xml.WORKLIST_IS_LOCKED_ATTR.value, "");
        }
        worklistElement.setAttribute(Xml.WORKLIST_FAVORABILITY_ATTR.value, favorability.getXmlConstantValue());
        return worklistElement;
    }

    // helper for toXml.
    private static Element createSectionListElement(final Document document,
                                                    Set<CourseSection> sectionObjects,
                                                    Schedule.Xml listName) {
        final Element sectionListElement = document.createElement(listName.getXmlConstantValue());
        for (CourseSection sectionObject : sectionObjects) {
            final Element sectionElement = document.createElement(Course.SecXml.COURSE_SECTION_TAG.getXmlConstantValue());
            sectionElement.setAttribute(
                    Course.SecXml.LECTURE_COMPLIMENTARY_SECTION_REF_ATTR.getXmlConstantValue(),
                    sectionObject.toString()); // See [Schedule]'s xml constructor and [CourseSectionRef.extractAndParseAll]
            sectionListElement.appendChild(sectionElement);
        }
        return sectionListElement;
    }



    public enum WorklistFavorability implements XmlUtils.XmlConstant {
        FAVORABLE ("^"),
        NEUTRAL ("~"),
        UNFAVORABLE ("v"),
        ;
        private final String xmlAttrVal;

        WorklistFavorability(String xmlAttrVal) {
            this.xmlAttrVal = xmlAttrVal;
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }

        /**
         * @param attr An Attr object. Must not be [null].
         * @return A [WorklistFavorability] whose [xmlAttrVal] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [WorklistFavorability] can be found.
         */
        public static WorklistFavorability decodeXmlAttr(Attr attr) throws MalformedXmlDataException {
            for (WorklistFavorability favorability : WorklistFavorability.values()) {
                if (favorability.xmlAttrVal.equals(attr.getValue())) {
                    return favorability;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }
    }

    public enum Xml implements XmlUtils.XmlConstant {
        WORKLIST_TAG("Worklist"), // for user data. do not use for data from UBC's registration site.
        WORKLIST_NAME_ATTR ("worklistName"),
        WORKLIST_IS_LOCKED_ATTR ("locked"), // parsing: true if attribute exists and false otherwise.
        WORKLIST_FAVORABILITY_ATTR ("favorability"), // parsing: see [WorklistFavorability.decodeXmlAttr]
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
