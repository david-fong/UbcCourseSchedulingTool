package org.bse.data.repr.courseutils;

import org.bse.data.repr.courseutils.Course.CourseSection;
import org.bse.data.repr.faculties.CampusNotFoundException;
import org.bse.data.repr.faculties.FacultyCourseNotFoundException;
import org.bse.data.repr.faculties.FacultyTreeNode;
import org.bse.data.repr.faculties.FacultyTreeRootCampus;
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

    boolean isLoaded();

    CourseSection dereference() throws FacultyCourseNotFoundException, CourseSectionNotFoundException;

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
        try {
            for (Element sectionRefElement : refElements) {
                sectionRefs.add(new CourseSectionRefUnloaded(sectionRefElement));
            }
        } catch (CampusNotFoundException e) {
            throw new MalformedXmlDataException(e);
        }

        return Collections.unmodifiableSet(sectionRefs);
    }


    /**
     * Construct with a string following the format:
     * "FACULTY COURSE SECTION", ex. "CPEN 211 T1A".
     * Each of the three tokens must not contain any spaces.
     */
    final class CourseSectionRefUnloaded implements CourseSectionRef {
        private final FacultyTreeNode facultyToken;
        private final String courseToken;
        private final String sectionToken;

        private CourseSectionRefUnloaded(final Element refElement) throws MalformedXmlDataException, CampusNotFoundException {
            this(XmlUtils.getMandatoryAttr(refElement, Xml.SECTION_REF_TO_ATTR).getValue());
        }

        private CourseSectionRefUnloaded(String refString) throws CampusNotFoundException {
            final String[] tokens = refString.split("\\s+");
            final FacultyTreeRootCampus campusToken = FacultyTreeRootCampus
                    .UbcCampuses.getCampusBySectionRefToken(tokens[0]);
            this.facultyToken = campusToken.getSquashedFacultyAbbrMap().get(tokens[1]);
            this.courseToken  = tokens[2];
            this.sectionToken = tokens[3];
        }

        @Override
        public boolean isLoaded() {
            return false;
        }

        @Override
        public CourseSection dereference() throws FacultyCourseNotFoundException, CourseSectionNotFoundException {
            return facultyToken
                    .getCourseByCodeString(courseToken)
                    .getSectionByIdToken(sectionToken);
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
