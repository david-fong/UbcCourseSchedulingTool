package org.bse.data.schedule;

import org.bse.data.courseutils.CourseSectionRef;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Set;

/**
 * A representation of a collection of [CourseSection]s. Completely immutable.
 * Instances can only be created through XML. The original XML will only be created
 * manually by the spider. For saving worklists, please see [ScheduleBuild.toXml].
 */
public final class Schedule implements ScheduleIf<CourseSectionRef> {

    static final String STT_NAME_FOR_SCHEDULE_WITHOUT_AN_STT = "N/A";

    private final Set<CourseSectionRef> courseSections; // unmodifiable.
    private final String sttName;
    private final Set<CourseSectionRef> sttSections; // unmodifiable.

    public Schedule(final Element scheduleElement) throws MalformedXmlDataException {
        final Element manualSectionListElement
                = XmlUtils.getOptionalUniqueChildByTag(
                        scheduleElement, Xml.MANUAL_SECTION_LIST_TAG
        );
        final Element sttSectionListElement
                = XmlUtils.getOptionalUniqueChildByTag(
                        scheduleElement, Xml.STT_SECTION_LIST_TAG
        );
        if (manualSectionListElement == null && sttSectionListElement == null) {
            throw new MalformedXmlDataException("A schedule must have one or both of a"
                    + " manually-added-sections-list and a STT-based-sections-list, but"
                    + " neither were found for the given Element.");
        }

        // manually-added section fields:
        final Set<CourseSectionRef> courseSections = CourseSectionRef.extractAndParseAll(manualSectionListElement);

        // standard timetable section fields:
        if (sttSectionListElement != null) {
            this.sttName = XmlUtils.getMandatoryAttr(sttSectionListElement, Xml.STT_NAME_ATTR).getValue();
            this.sttSections = Collections.unmodifiableSet(
                    CourseSectionRef.extractAndParseAll(sttSectionListElement)
            );
        } else {
            this.sttName = STT_NAME_FOR_SCHEDULE_WITHOUT_AN_STT;
            this.sttSections = Collections.emptySet();
        }
        courseSections.addAll(sttSections);
        this.courseSections = courseSections;
    }

    /**
     * @return An unmodifiable view of the [Set] of
     *     [CourseSection]s contained in this [Schedule].
     */
    public final Set<CourseSectionRef> getCourseSections() {
        return courseSections;
    }

    public final boolean isBasedOffAnStt() {
        return !sttSections.isEmpty();
    }

    public final String getEnclosedSttName() {
        return sttName;
    }

    /**
     * @return Not null.
     */
    public Set<CourseSectionRef> getEnclosedSttSections() {
        return sttSections;
    }

    /**
     * @return A [ScheduleBuild] suitable for use generating schedule builds
     *     containing all the schedules of [this][Schedule] at the time of the
     *     method call. Returns [null] if a problem with de-referencing occurs.
     */
    public final ScheduleBuild createPickyBuildTemplate() {
        try {
            return new ScheduleBuild(this);
        } catch (MalformedXmlDataException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    TODO [api][Schedule] methods to export contents to csv files, google calendar files.
     */



    // TODO [xml:spec]
    public enum Xml implements XmlUtils.XmlConstant {
        SCHEDULE_TAG("Schedule"),
        MANUAL_SECTION_LIST_TAG ("Sections"), // optional if there are stt sections.
        STT_SECTION_LIST_TAG ("SttSections"), // optional if there are manually added sections.
        STT_NAME_ATTR ("sttName"), // mandatory for the [STT_SECTION_LIST_TAG] element if it exists.
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
