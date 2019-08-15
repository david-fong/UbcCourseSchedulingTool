package org.bse.data.repr.courseutils;

import org.bse.data.repr.courseutils.Course.CourseSection;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Used to allow [CourseSchedule]s and [CourseLectureSection]s to refer to
 * [CourseSection]s that may not have been lazily loaded yet, and demand that
 * the referred [CourseSection] be loaded through the [dereference] method.
 */
public interface CourseSectionRef {

    CourseSection dereference();

    default boolean overlapsWith(CourseSectionRef other) {
        return dereference().overlapsWith(other.dereference());
    }

    /**
     * Used to unpack [Schedule] data created by the Spider, or [Worklist] data
     * created by the user and stored by the application.
     *
     * @param host An Element expected to contain some number (perhaps zero) of
     *     child [Element]s formatted according to the [CourseSectionRef]
     *     [Element] format spec. If null, returns a modifiable empty [Set].
     * @return An unmodifiable [Set] of all [CourseSectionRef]s successfully
     *     parsed from recognized [Element]s directly under [host].
     * @throws MalformedXmlDataException If any [Element]s by the [CourseSectionRef]
     *     tag name were found but did not follow the rest of the format spec.
     */
    static Set<CourseSectionRef> extractAndParseAll(final Element host) throws MalformedXmlDataException {
        if (host == null) return new HashSet<>();
        final List<Element> refElements = XmlUtils.getChildElementsByTagName(
                host, Xml.SECTION_REF_TAG
        );
        final Set<CourseSectionRef> sectionRefs = new HashSet<>(refElements.size());
        for (Element sectionRefElement : refElements) {
            sectionRefs.add(new CourseSectionRefUnloaded(sectionRefElement));
        }
        return Collections.unmodifiableSet(sectionRefs);
    }



    final class CourseSectionRefUnloaded implements CourseSectionRef {

        private final String refString; // TODO: change rep to make this a faculty, a Course name, and a section name.

        private CourseSectionRefUnloaded(final Element refElement) throws MalformedXmlDataException {
            this(XmlUtils.getMandatoryAttr(refElement, Xml.SECTION_REF_TO_ATTR).getValue());
        }

        private CourseSectionRefUnloaded(String refString) {
            this.refString = refString;
        }

        @Override
        public CourseSection dereference() {
            return null; // TODO [impl] once the squashed campus-faculty lookup system is up.
        }
    }



    enum Xml implements XmlUtils.XmlConstant {
        SECTION_REF_TAG ("SectionRef"),
        SECTION_REF_TO_ATTR ("to"),
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
