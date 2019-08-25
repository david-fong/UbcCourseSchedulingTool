package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Attr;

import java.time.*;
import java.time.format.DateTimeFormatter;
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
        public final int value;
        public final String label;
        private final String xmlAttrVal;

        YearOfStudy(String label) {
            this.value = ordinal() + 1;
            this.label = label;
            this.xmlAttrVal = this.label.split("\\s+")[0];
        }

        @Override
        public String toString() {
            return label;
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [YearOfStudy] whose [label] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [YearOfStudy] can be found.
         */
        public static YearOfStudy decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            final String attrToken = attr.getValue().split("\\s+")[0];
            for (YearOfStudy yearOfStudy : values()) {
                if (yearOfStudy.label.equals(attrToken)) {
                    return yearOfStudy;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
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
        public final Session session;
        public final Term term;
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

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }

        /**
         * All month days for the dates returned by this method from the same instance
         * will fall in the same week.
         * @param year A year like "2019".
         * @param weekDay The [WeekDay] to get the approximate first day of class for.
         * @return A [LocalDate] with the given [year] at the beginning of [this]
         *     [Semester] on the given [WeekDay].
         */
        public LocalDate getApproxClassStartDay(final int year, final WeekDay weekDay) {
            return LocalDate.of(year, startMonth, 1)
                    .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
                    .plus(startWeek, ChronoUnit.WEEKS)
                    .with(TemporalAdjusters.nextOrSame(weekDay.dayOfWeek));
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [Semester] whose [xmlAttrVal] is equal to [attr::getValue].
         * @throws MalformedXmlDataException if no such [Semester] can be found.
         */
        public static Semester decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            for (Semester semester : Semester.values()) {
                if (semester.xmlAttrVal.equals(attr.getValue())) {
                    return semester;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }

        public static Semester getCurrentSemester(final LocalDate date) {
            final Map<LocalDate, Semester> dateSemesterMap = new HashMap<>();
            final List<LocalDate> semesterLoop = Arrays.stream(values())
                    .map(semester -> {
                        final LocalDate semesterDate = semester.getApproxClassStartDay(date.getYear(), WeekDay.MONDAY);
                        dateSemesterMap.put(semesterDate, semester);
                        return semesterDate;
                    })
                    .sorted().collect(Collectors.toList());
            semesterLoop.add(semesterLoop.get(0).plus(1, ChronoUnit.YEARS));

            for (int i = 0; i < values().length; i++) {
                if (date.compareTo(semesterLoop.get(i)) >= 0 && date.compareTo(semesterLoop.get(i + 1)) < 0) {
                    return dateSemesterMap.get(semesterLoop.get(i));
                }
            }
            throw new RuntimeException("Unable to get current semester. Should never reach here.");
        }

        public enum Session {
            WINTER ("Winter"),
            SUMMER ("Summer"),
            ;
            public final String label;

            Session(String label) {
                this.label = label;
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
            public final int value;
            public final String label;

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

        WeekDay(String xmlAttrVal) {
            this.xmlAttrVal = xmlAttrVal;
            this.dayOfWeek = DayOfWeek.of(ordinal() + 1);
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [WeekDay] whose [getXmlConstantValue] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [WeekDay] can be found.
         */
        public static WeekDay decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            for (WeekDay weekDay : WeekDay.values()) {
                if (weekDay.xmlAttrVal.equals(attr.getValue())) {
                    return weekDay;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }
    }



    /**
     * Utilities for the UBC Timezone.
     */
    public static final ZoneId UBC_TIMEZONE_ID = ZoneId.of("America/Vancouver");

    public static ZonedDateTime getCurrentUbcDateTime() {
        return ZonedDateTime.now(UBC_TIMEZONE_ID);
    }

    public enum BlockTime implements XmlUtils.XmlConstant {
        T0700, T0730, T0800, T0830,
        T0900, T0930, T1000, T1030,
        T1100, T1130, T1200, T1230,
        T1300, T1330, T1400, T1430,
        T1500, T1530, T1600, T1630,
        T1700, T1730, T1800, T1830,
        T1900, T1930, T2000, T2030,
        T2100, T2130,
        ;
        private static final int EARLIEST_BLOCK_HOUR = 7;
        private static final DateTimeFormatter MY_12_HOUR_CLOCK_FORMATTER;
        private static final DateTimeFormatter MY_24_HOUR_CLOCK_FORMATTER;
        static {
            MY_12_HOUR_CLOCK_FORMATTER = DateTimeFormatter.ofPattern("h:mma");
            MY_24_HOUR_CLOCK_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        }
        private final OffsetTime time;

        BlockTime() {
            final int hour   = (ordinal() / 2) + EARLIEST_BLOCK_HOUR;
            final int minute = (ordinal() % 2 == 0) ? 0 : 30;
            this.time = ZonedDateTime.of(
                    0, 0, 0,
                    hour, minute, 0, 0,
                    UBC_TIMEZONE_ID
            ).toOffsetDateTime().toOffsetTime();
        }

        public boolean isBefore(BlockTime other) {
            return ordinal() < other.ordinal();
        }

        public boolean isAfter(BlockTime other) {
            return ordinal() > other.ordinal();
        }

        public OffsetTime getTime() {
            return time;
        }

        public String get12HourTimeString() {
            return time.format(MY_12_HOUR_CLOCK_FORMATTER).toLowerCase();
        }

        public String get24HrTimeString() {
            return time.format(MY_24_HOUR_CLOCK_FORMATTER);
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [BlockTime] whose [getXmlConstantValue] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [BlockTime] can be found.
         */
        public static BlockTime decodeXmlAttr(Attr attr) throws MalformedXmlDataException {
            for (BlockTime blockTime : BlockTime.values()) {
                if (blockTime.getXmlConstantValue().equals(attr.getValue())) {
                    return blockTime;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }

        /**
         * @return A zero-padded time string in 24-hour format such as "15:27" (3:27pm).
         */
        @Override
        public String getXmlConstantValue() {
            return get24HrTimeString();
        }
    }

}
