package com.dvf.ucst.core.schedule;

import com.dvf.ucst.core.courseutils.CourseSectionBlock;
import com.dvf.ucst.utils.calendar.GoogleCalCsvColumns;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.courseutils.Course.CourseSection;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A mutable wrapper for a [Schedule] object. While this class
 * can be used for generating schedules in the [PickyBuildGenerator], it
 * is not intended for such use, which should be left to [ScheduleBuild].
 *
 * Implementation note: names are immutable and assigned as-is from the
 * corresponding constructor argument, but for the sake of robustness, code
 * in this package (the only place instances of this class can be constructed)
 * should not make that assumption. Names must never be [null].
 */
public final class Worklist extends ScheduleBuild implements XmlUtils.UserDataXml {

    /**
     * Not intended to be safe for (or to be used as) file names.
     * Must not be an empty string.
     * Must begin with an alphabetic character.
     * Permits non-contiguous intermediate spaces.
     * Permits numbers and punctuation marks.
     */
    public static final Predicate<String> PERMITTED_NAME_TESTER
            = Pattern.compile("\\p{Alpha}([ ]?\\p{Graph})*").asMatchPredicate();

    private final String name;
    private boolean isLocked = false;
    private WorklistFavorability favorability = WorklistFavorability.NEUTRAL;
    // ^does not effect internal behaviour.

    // Up-cast / copy constructor:
    Worklist(final ScheduleBuild otherSchedule, final String name) throws MalformedWorklistNameArgumentException {
        super(otherSchedule);
        assert name != null : "Worklist name constructor arg must never be null";
        if (!PERMITTED_NAME_TESTER.test(name)) {
            throw new MalformedWorklistNameArgumentException();
        }
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
        final String name = XmlUtils.getMandatoryAttr(worklistElement, Xml.WORKLIST_NAME_ATTR).getValue();
        if (!PERMITTED_NAME_TESTER.test(name)) {
            throw new MalformedXmlDataException(new MalformedWorklistNameArgumentException());
        }
        this.name = name;
        this.isLocked = worklistElement.hasAttribute(Xml.WORKLIST_IS_LOCKED_ATTR.value);
        this.favorability = WorklistFavorability.decodeXmlAttr(
                XmlUtils.getMandatoryAttr(
                        worklistElement,
                        Xml.WORKLIST_FAVORABILITY_ATTR
                )
        );
    }

    // For users to create from scratch:
    Worklist(final String name) throws MalformedWorklistNameArgumentException {
        super();
        assert name != null : "Worklist name constructor arg must never be null";
        if (!PERMITTED_NAME_TESTER.test(name)) {
            throw new MalformedWorklistNameArgumentException();
        }
        this.name = name;
    }

    // not used.
    @Override
    public Worklist copy() {
        try {
            return new Worklist(this, name);
        } catch (MalformedWorklistNameArgumentException e) {
            throw new RuntimeException(String.format("Unexpected %s: an existing %s"
                    + " should only have been successfully constructed with a valid name,"
                    + " whose validity should be held true afterwards since the predicate"
                    + " uses purely combinational logic. The failed name was \"%s\"",
                    MalformedWorklistNameArgumentException.class.getName(),
                    Worklist.class.getName(), name
            ));
        }
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

//    /**
//     * @return An immutable snapshot of an implementing instance, WITHOUT ANY OF ITS
//     *     UNIQUE BEHAVIOUR. This may be called, for instance, when a student has
//     *     successfully registered into a [Worklist] (a subclass of [ScheduleBuild]),
//     *     and no longer requires any of its mutable behaviour.
//     */
//    public final Schedule createImmutableCopy() {
//        return new Schedule(this);
//    }

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

    /**
     * Create a GoogleCalendar-compatible .csv String.
     */
    public final String toGoogleCalendarCsvString() {
        final List<EnumMap<GoogleCalCsvColumns, String>> rows = new ArrayList<>();
        for (final CourseSection section : getCourseSections()) {
            for (final CourseSectionBlock block : section.getBlocks()) {
                final String startDateString = GoogleCalCsvColumns.DATE_FORMAT.format(
                        section.getSemester().getApproxClassStartDay(
                                LocalDate.now().getYear(), block.getWeekDay())
                );
                final EnumMap<GoogleCalCsvColumns, String> row = new EnumMap<>(GoogleCalCsvColumns.class);
                row.put(GoogleCalCsvColumns.SUBJECT,    section.getUserFullSectionIdString());
                row.put(GoogleCalCsvColumns.START_DATE, startDateString);
                row.put(GoogleCalCsvColumns.START_TIME, block.getStartTime().googleCalCsvString);
                row.put(GoogleCalCsvColumns.END_TIME,   block.getEndTime().googleCalCsvString);
                //row.put(GoogleCalCsvColumns.LOCATION, block.getLocation());
                row.put(GoogleCalCsvColumns.DESCRIPTION, section.getParentCourse().getCourseDescription());
                rows.add(row);
            }
        }
        return GoogleCalCsvColumns.getCalendarString(INCLUDED_GOOGLE_CALENDAR_COLUMNS, rows);
    }
    private static final List<GoogleCalCsvColumns> INCLUDED_GOOGLE_CALENDAR_COLUMNS = List.of(
            GoogleCalCsvColumns.SUBJECT, GoogleCalCsvColumns.START_DATE,
            GoogleCalCsvColumns.START_TIME, GoogleCalCsvColumns.END_TIME,
            /*GoogleCalCsvColumns.LOCATION, */GoogleCalCsvColumns.DESCRIPTION
    );

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



    static final class MalformedWorklistNameArgumentException extends Exception {
        MalformedWorklistNameArgumentException() {
            super(String.format("Invalid name format. See %s for more info on formatting",
                    Worklist.class.getName()
            ));
        }
    }

    /**
     *
     */
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
