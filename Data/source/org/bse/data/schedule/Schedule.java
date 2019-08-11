package org.bse.data.schedule;

import org.bse.data.repr.courseutils.Course.CourseSection;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlParsingUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A representation of a collection of [CourseSection]s.
 * COMPLETELY immutable from a public point of view,
 * where instances can only be created through XML.
 */
public class Schedule {

    final Set<CourseSection> courseSections;
    private final Set<CourseSection> unmodifiableSectionsView;

    // TODO [xml:read][Schedule]:
    public Schedule(Element scheduleElement) throws MalformedXmlDataException {
        final List<Element> refElements = XmlParsingUtils.getElementsByTagName(scheduleElement, Xml.SECTION_REF_TAG);
        final List<String> sectionRefs = new ArrayList<>(refElements.size());
        for (Element sectionRefElement : refElements) {
            sectionRefs.add(XmlParsingUtils.getMandatoryAttr(sectionRefElement, Xml.SECTION_REF_TO_ATTR).getValue());
        }
        this.courseSections = new HashSet<>();
        // TODO: ^implement. Also, should the entry type be changed to a new class for string-based references?
        this.unmodifiableSectionsView = Collections.unmodifiableSet(courseSections);
    }

    /**
     * IMPORTANT: Does not defensively copy.
     *
     * @param courseSections A collection of [CourseSection]s to include in this
     *     [Schedule] from the start.
     */
    Schedule(Set<CourseSection> courseSections) {
        this.courseSections = courseSections;
        this.unmodifiableSectionsView = Collections.unmodifiableSet(this.courseSections);
    }

    /**
     * @return An unmodifiable view of the [Set] of
     *     [CourseSection]s contained in this [Schedule].
     */
    public final Set<CourseSection> getCourseSections() {
        return unmodifiableSectionsView;
    }

    /**
     * @return Whether this [CourseSection] is based off of an STT (Ie. a subset of
     *     its [CourseSection]s cannot be removed. The value returned by this method
     *     must never vary when called from the same instance multiple times.
     */
    public boolean isBasedOffAnStt() {
        return false;
    }

    /**
     * @return A [ScheduleBuild] suitable for use generating schedule builds
     *     containing all the schedules of [this][Schedule] at the time of the
     *     method call.
     */
    public ScheduleBuild createPickyBuildTemplate() {
        return new ScheduleBuild(this);
    }

    /**
     * @return An immutable snapshot of an implementing instance, WITHOUT ANY OF ITS
     *     UNIQUE BEHAVIOUR. This may be called, for instance, when a student has
     *     successfully registered into a [Worklist] (a subclass of [ScheduleBuild]),
     *     and no longer requires any of its mutable behaviour.
     */
    public final Schedule createImmutableCopy() {
        return new Schedule(new HashSet<>(getCourseSections())) {
            private final boolean isBasedOffAnStt = Schedule.this.isBasedOffAnStt();
            @Override
            public boolean isBasedOffAnStt() {
                return isBasedOffAnStt;
            }
        };
    }



    /**
     *
     */
    public static final class SttSchedule extends Schedule {

        private final String name;
        private final Set<CourseSection> sttSections;

        public SttSchedule(Element sttElement) throws MalformedXmlDataException {
            super(sttElement);
            this.name = XmlParsingUtils.getMandatoryAttr(sttElement, Xml.STT_NAME_ATTR).getValue();
            this.sttSections = Collections.unmodifiableSet(new HashSet<>(getCourseSections())); // snapshot.
        }

        public final String getName() {
            return name;
        }

        @Override
        public boolean isBasedOffAnStt() {
            return true;
        }

        @Override
        public ScheduleBuild.SttScheduleBuild createPickyBuildTemplate() {
            return new ScheduleBuild.SttScheduleBuild(this);
        }

        public Set<CourseSection> getSttSections() {
            return sttSections;
        }
    }

    // TODO [xml:spec]
    public enum Xml implements XmlParsingUtils.XmlConstant {
        SCHEDULE_TAG("Schedule"),

        MANUAL_SECTION_LIST_TAG ("Sections"), // optional if there are stt sections.
        STT_SECTION_LIST_TAG ("SttSections"), // optional if there are manually added sections.
        STT_NAME_ATTR ("sttName"), // mandatory for the [STT_SECTION_LIST_TAG] element if it exists.

        // contents of section-list elements:
        SECTION_REF_TAG ("SectionRef"),
        SECTION_REF_TO_ATTR ("to"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

}
