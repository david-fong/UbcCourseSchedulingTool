package org.bse.data.schedule;

import org.bse.data.repr.courseutils.Course.CourseSection;
import org.bse.data.repr.courseutils.CourseSectionRef;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

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

    Worklist(Schedule otherSchedule, String name) {
        super(otherSchedule);
        this.name = name;

        if (otherSchedule instanceof Worklist) {
            final Worklist otherWorklist = (Worklist)otherSchedule;
            this.isLocked = otherWorklist.isLocked;
            this.favorability = otherWorklist.favorability;
        }
    }

    public Worklist(Element worklistElement) throws MalformedXmlDataException {
        super(worklistElement);
        this.name = XmlUtils.getOptionalAttr(
                worklistElement,
                Xml.WORKLIST_NAME_ATTR,
                Xml.WORKLIST_NAME_ATTR_DEFAULT.value
        );
        this.isLocked = worklistElement.getAttribute(Xml.WORKLIST_IS_LOCKED_ATTR.value) != null;
        this.favorability = WorklistFavorability.decodeXmlAttr(
                XmlUtils.getMandatoryAttr(
                        worklistElement,
                        Xml.WORKLIST_FAVORABILITY_ATTR
                )
        );
    }

    @Override
    public Worklist copy() {
        return new Worklist(this, name);
    }

    @Override
    public final boolean conflictsWithAny(CourseSectionRef section) {
        // disables adding [CourseSection]s if locked:
        return isLocked || super.conflictsWithAny(section);
    }

    /**
     * @param section A [CourseSection] to attempt to remove from this [Worklist].
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

    // TODO [xml:write][Worklist]
    @Override
    public Element toXml() {
        return null;
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
        WORKLIST_TAG_NAME ("Worklist"), // for user data. do not use for data from UBC's registration site.
        WORKLIST_NAME_ATTR ("worklistName"), // optional for the [MANUAL_SECTION_LIST_TAG] element if it exists.
        WORKLIST_NAME_ATTR_DEFAULT ("unnamed"),
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
