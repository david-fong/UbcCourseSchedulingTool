package org.bse.core.registration;

import org.bse.core.utils.FacultyTreeNodeIf;
import org.bse.core.utils.HyperlinkBookIf;
import org.bse.requirement.Requirement;

import java.util.Set;

public abstract class CourseIf implements CreditValued {

    abstract FacultyTreeNodeIf getFacultyTreeNode();
    abstract CourseCode getCourseCode();
    abstract CourseUtils.CourseType getCourseType();

    abstract String getCourseDescription();
    abstract HyperlinkBookIf getHyperlinkBook();
    // abstract int getCreditValue();

    /**
     *
     * @return These may return null if they impose no requirements.
     */
    abstract Requirement<Set<CourseSchedule>> getPreRequisites();
    abstract Requirement<CourseSchedule> getCoRequisites();



    public static final class CourseCode {

        private final int year;
        private final int value;
        private final String suffix;

        public CourseCode(int value) {
            // TODO: is this calculation for year always correct?
            this(value / 100, value, "");
        }

        public CourseCode(int year, int value, String suffix) {
            this.year = year;
            this.value = value;
            this.suffix = suffix;
        }

        public int getYear() {
            return year;
        }

        public int getValue() {
            return value;
        }

        public String getSuffix() {
            return suffix;
        }

        @Override
        public String toString() {
            return value + suffix;
        }
    }

}
