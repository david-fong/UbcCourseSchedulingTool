package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Attr;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility enums for time-related constants at UBC.
 */
public final class CourseUtils {

    public enum YearOfStudy implements XmlUtils.XmlConstant {
        FIRST_YEAR  ("First year"),
        SECOND_YEAR ("Second year"),
        THIRD_YEAR  ("Third year"),
        FOURTH_YEAR ("Fourth year"),
        FIFTH_YEAR  ("Fifth year"),
        SIXTH_YEAR  ("Sixth year"),
        SEVENTH_YEAR("Seventh year"), // :0
        ;
        private final int value;
        private final String label;
        private final String xmlAttrVal;

        YearOfStudy(final String label) {
            this.value = ordinal() + 1;
            this.label = label;
            this.xmlAttrVal = this.label.split("\\s+")[0];
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [YearOfStudy] whose [label] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [YearOfStudy] can be found.
         */
        public static YearOfStudy decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            final String attrToken = attr.getValue().split("\\s+")[0];
            for (final YearOfStudy yearOfStudy : values()) {
                if (yearOfStudy.getXmlConstantValue().equals(attrToken)) {
                    return yearOfStudy;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }

        public int getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /**
     * Used in [CourseSectionBlock].
     * Has a [Session] and a [Term].
     */
    public enum Semester implements XmlUtils.XmlConstant {
        SUMMER_S1 (Session.SUMMER, Term.TERM_ONE, "S1", Month.MAY, 1),
        SUMMER_S2 (Session.SUMMER, Term.TERM_TWO, "S2", Month.JULY, 0),
        WINTER_S1 (Session.WINTER, Term.TERM_ONE, "W1", Month.SEPTEMBER, 0),
        WINTER_S2 (Session.WINTER, Term.TERM_TWO, "W2", Month.JANUARY, 0),
        ;
        private final Session session;
        private final Term term;
        private final String xmlAttrVal;
        private final Month startMonth;
        private final int startWeek;

        Semester(Session session, Term term, String xmlAttrVal, Month startMonth, int startWeek) {
            this.session = session;
            this.term = term;
            this.xmlAttrVal = xmlAttrVal;
            this.startMonth = startMonth;
            this.startWeek = startWeek;
        }

        /**
         * All month days for the dates returned by this method from the same instance
         * will fall in the same week.
         * @param year A year like "2019".
         * @param weekDay The [WeekDay] to get the approximate first day of class for.
         * @return A [LocalDate] with the given [year] at the beginning of [this]
         *     [Semester] on the given [WeekDay].
         */
        public ZonedDateTime getApproxClassStartDay(final int year, final WeekDay weekDay) {
            return LocalDate.of(year, startMonth, 1)
                    .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
                    .plusWeeks(startWeek)
                    .with(TemporalAdjusters.nextOrSame(weekDay.dayOfWeek))
                    .atStartOfDay(UbcTimeUtils.UBC_TIMEZONE_ZONE_ID)
                    ;
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [Semester] whose [xmlAttrVal] is equal to [attr::getValue].
         * @throws MalformedXmlDataException if no such [Semester] can be found.
         */
        public static Semester decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            for (Semester semester : Semester.values()) {
                if (semester.getXmlConstantValue().equals(attr.getValue())) {
                    return semester;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }

        public static Semester getCurrentSemester(final ZonedDateTime zonedDate) {
            final Map<ZonedDateTime, Semester> dateSemesterMap = new HashMap<>();
            final List<ZonedDateTime> semesterLoop = Arrays.stream(values())
                    .map(semester -> {
                        final ZonedDateTime semesterDate = semester
                                .getApproxClassStartDay(zonedDate.getYear(), WeekDay.MONDAY);
                        dateSemesterMap.put(semesterDate, semester);
                        return semesterDate;
                    })
                    .sorted().collect(Collectors.toList());
            semesterLoop.add(semesterLoop.get(0).plus(1, ChronoUnit.YEARS));

            for (int i = 0; i < values().length; i++) {
                if (zonedDate.compareTo(semesterLoop.get(i)) >= 0 &&
                        zonedDate.compareTo(semesterLoop.get(i + 1)) < 0) {
                    return dateSemesterMap.get(semesterLoop.get(i));
                }
            }
            throw new RuntimeException("Unable to get current semester. Should never reach here.");
        }

        public Session getSession() {
            return session;
        }

        public Term getTerm() {
            return term;
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }

        public enum Session {
            WINTER ("Winter"),
            SUMMER ("Summer"),
            ;
            private final String label;

            Session(String label) {
                this.label = label;
            }

            public String getLabel() {
                return label;
            }

            @Override
            public String toString() {
                return label;
            }
        }

        public enum Term {
            TERM_ONE,
            TERM_TWO,
            ;
            private final int value;
            private final String label;

            Term() {
                this.value = ordinal() + 1;
                this.label = "Term " + this.value;
            }

            /**
             * @param val Should be 1 or 2.
             * @return returns null if an unexpected value is given.
             */
            public static Term getTermByValue(int val) {
                val--;
                return (val > 0 && val < values().length)
                        ? values()[val - 1] : null;
            }

            public int getValue() {
                return value;
            }

            public String getLabel() {
                return label;
            }

            @Override
            public String toString() {
                return label;
            }
        }
    }

    public enum WeekDay implements XmlUtils.XmlConstant {
        MONDAY ("mon"),
        TUESDAY ("tues"),
        WEDNESDAY ("wed"),
        THURSDAY ("thurs"),
        FRIDAY ("fri"),
        ;
        private final String xmlAttrVal;
        private final DayOfWeek dayOfWeek;

        WeekDay(final String xmlAttrVal) {
            this.xmlAttrVal = xmlAttrVal;
            this.dayOfWeek = DayOfWeek.of(ordinal() + 1);
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [WeekDay] whose [getXmlConstantValue] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [WeekDay] can be found.
         */
        public static WeekDay decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            for (final WeekDay weekDay : WeekDay.values()) {
                if (weekDay.getXmlConstantValue().equals(attr.getValue())) {
                    return weekDay;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }
    }

}
