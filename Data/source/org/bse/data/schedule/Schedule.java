package org.bse.data.schedule;

import org.bse.data.repr.courseutils.CourseSectionRef;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A representation of a collection of [CourseSection]s.
 * COMPLETELY immutable from a public point of view,
 * where instances can only be created through XML.
 */
public class Schedule {

    final Set<CourseSectionRef> courseSections;
    private final Set<CourseSectionRef> publicSectionsView;
    private final String sttName;
    private final Set<CourseSectionRef> sttSections; // unmodifiable.

    public Schedule(Element scheduleElement) throws MalformedXmlDataException {
        final Element sectionListElement
                = XmlUtils.getOptionalUniqueElementByTag(
                        scheduleElement, Xml.MANUAL_SECTION_LIST_TAG
        );
        final Element sttSectionListElement
                = XmlUtils.getOptionalUniqueElementByTag(
                        scheduleElement, Xml.STT_SECTION_LIST_TAG
        );
        if (sectionListElement == null && sttSectionListElement == null) {
            throw new MalformedXmlDataException("A schedule must have one or both of a"
                    + " manually-added-sections-list and a STT-based-sections-list, but"
                    + " neither were found for the given Element.");
        }

        // manually-added section fields:
        this.courseSections = CourseSectionRef.extractAndParseAll(sectionListElement);
        this.publicSectionsView = Collections.unmodifiableSet(courseSections);

        // standard timetable section fields:
        if (sttSectionListElement != null) {
            this.sttName = XmlUtils.getMandatoryAttr(scheduleElement, Xml.STT_NAME_ATTR).getValue();
            this.sttSections = CourseSectionRef.extractAndParseAll(sttSectionListElement);
        } else {
            this.sttName = "N/A";
            this.sttSections = Collections.emptySet();
        }
        this.courseSections.addAll(sttSections);
    }

    /**
     * Defensively shallow-copies.
     */
    Schedule(Schedule other) {
        this.courseSections = new HashSet<>(other.getCourseSections());
        this.publicSectionsView = Collections.unmodifiableSet(this.courseSections);
        this.sttName = other.sttName;
        this.sttSections = other.sttSections;
    }

    /**
     * @return An unmodifiable view of the [Set] of
     *     [CourseSection]s contained in this [Schedule].
     */
    public final Set<CourseSectionRef> getCourseSections() {
        return publicSectionsView;
    }

    /**
     * @return Whether this [CourseSection] is based off of an STT (Ie. a subset of
     *     its [CourseSection]s cannot be removed. The value returned by this method
     *     must never vary when called from the same instance multiple times.
     */
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
     *     method call.
     */
    public final ScheduleBuild createPickyBuildTemplate() {
        return new ScheduleBuild(this);
    }

    /**
     * @return An immutable snapshot of an implementing instance, WITHOUT ANY OF ITS
     *     UNIQUE BEHAVIOUR. This may be called, for instance, when a student has
     *     successfully registered into a [Worklist] (a subclass of [ScheduleBuild]),
     *     and no longer requires any of its mutable behaviour.
     */
    public final Schedule createImmutableCopy() {
        return new Schedule(this);
    }

    /*
    TODO [api][Schedule] methods to export contents to csv files, google calendar files.
     */

    // TODO [xml:write][Schedule]
    void populateXmlElement(final Document document, final Element unpopulatedScheduleElement) {
    }



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
