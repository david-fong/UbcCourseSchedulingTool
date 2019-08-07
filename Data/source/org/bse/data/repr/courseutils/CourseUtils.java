package org.bse.data.repr.courseutils;

import org.bse.utils.xml.MalformedXmlDataException;
import org.w3c.dom.Attr;

/**
 * TODO [doc]: write documentation.
 */
public final class CourseUtils {

    public enum YearOfStudy {
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

        YearOfStudy(String label) {
            this.value = ordinal() + 1;
            this.label = label;
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
    public enum Semester {
        WINTER_S1 (Session.WINTER, Term.TERM_ONE, "W1"),
        WINTER_S2 (Session.WINTER, Term.TERM_TWO, "W2"),
        SUMMER_S1 (Session.SUMMER, Term.TERM_ONE, "S1"),
        SUMMER_S2 (Session.SUMMER, Term.TERM_TWO, "S2"),
        ;
        public final Session session;
        public final Term term;
        public final String xmlAttrVal;

        Semester(Session session, Term term, String xmlAttrVal) {
            this.session = session;
            this.term = term;
            this.xmlAttrVal = xmlAttrVal;
        }

        /**
         * @param attr An Attr object. Must not be null.
         * @return A [Semester] whose [xmlAttrVal] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [Semester] can be found.
         */
        public static Semester decodeXmlAttr(Attr attr) throws MalformedXmlDataException {
            for (Semester semester : Semester.values()) {
                if (semester.xmlAttrVal.equals(attr.getValue())) {
                    return semester;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }
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

    /**
     * A term in a Session. Either 1 or 2.
     */
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

    private static final int EARLIEST_BLOCK_HOUR = 7;

    public enum BlockTime {
        T0700, T0730,
        T0800, T0830,
        T0900, T0930,
        T1000, T1030,
        T1100, T1130,
        T1200, T1230,
        T1300, T1330,
        T1400, T1430,
        T1500, T1530,
        T1600, T1630,
        T1700, T1730,
        T1800, T1830,
        T1900, T1930,
        T2000, T2030,
        T2100, T2130,
        ;
        public final int hour;
        public final int minute;

        BlockTime() {
            this.hour = (ordinal() / 2) + EARLIEST_BLOCK_HOUR;
            this.minute = (ordinal() % 2 == 0) ? 0 : 30;
        }

        public boolean isBefore(BlockTime other) {
            return ordinal() < other.ordinal();
        }

        public boolean isAfter(BlockTime other) {
            return ordinal() > other.ordinal();
        }

        /**
         * @param attr An Attr object. Must not be null.
         * @return A [BlockTime] whose [xmlAttrVal] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [BlockTime] can be found.
         */
        public static BlockTime decodeXmlAttr(Attr attr) throws MalformedXmlDataException {
            final String enumName = "T" + attr.getValue(); // TODO [xml:spec][BlockTime] decide on format.
            try {
                return BlockTime.valueOf(enumName);
            } catch (IllegalArgumentException e) {
                throw MalformedXmlDataException.invalidAttrVal(attr);
            }
        }

        // TODO [impl]: Added string getter methods (and fields?)
    }

}
