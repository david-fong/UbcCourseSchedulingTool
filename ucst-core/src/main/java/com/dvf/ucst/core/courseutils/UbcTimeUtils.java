package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Attr;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Defines utilities to get the absolute date/time at UBC regardless of the timezone
 * experienced by the user, and an enumeration of the set of [BlockTime]s used in
 * standard UBC scheduling.
 */
public final class UbcTimeUtils {

    public static final String UBC_TIMEZONE_ID_STRING = "America/Vancouver";

    public static ZoneOffset getUbcTimezoneOffset() {
        return ZoneId.of(UBC_TIMEZONE_ID_STRING).getRules().getOffset(LocalDateTime.now());
    }

    public static ZonedDateTime getCurrentUbcDateTime() {
        return ZonedDateTime.now(getUbcTimezoneOffset());
    }

    public static OffsetTime getCurrentUbcTime() {
        return getCurrentUbcDateTime().toOffsetDateTime().toOffsetTime();
    }



    /**
     * Enumeration of standard block times used in UBC scheduling.
     * https://facultystaff.students.ubc.ca/enrolment-services/scheduling-records-systems-management/scheduling-services/course-scheduling/academic-course-scheduling-guidelines#standard-times
     */
    public enum BlockTime implements XmlUtils.XmlConstant {
        T0800, T0830, T0900, T0930,
        T1000, T1030, T1100, T1130,
        T1200, T1230, T1300, T1330,
        T1400, T1430, T1500, T1530,
        T1600, T1630, T1700, T1730,
        T1800, T1830, T1900, T1930,
        T2000, T2030, T2100, T2130,
        OUTSIDE_REGULAR_CLASS_TIME,
        ;
        private static final int EARLIEST_BLOCK_HOUR = 8; // anchors first enum.
        private static final DateTimeFormatter MY_12_HOUR_CLOCK_FORMATTER = DateTimeFormatter.ofPattern("h:mma");
        private static final DateTimeFormatter MY_24_HOUR_CLOCK_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

        private final OffsetTime time;

        BlockTime() {
            final int hour = (ordinal() / 2) + EARLIEST_BLOCK_HOUR;
            final int minute = (ordinal() % 2 == 0) ? 0 : 30;
            this.time = OffsetTime.of(hour, minute, 0, 0, getUbcTimezoneOffset());
        }

        public boolean isBefore(final BlockTime other) {
            return ordinal() < other.ordinal();
        }

        public boolean isAfter(final BlockTime other) {
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
         * @param currentTime The [OffsetTime] to use as the current time for this
         *     operation. Must not be [null].
         * @return The [BlockTime] in UBC's timezone at the current instant. Returns
         *     [END_OF_DAY] if [currentTime] does not fall between the start-times of
         *     two ordinally-contiguous [BlockTime]s. If [UbcTimeUtils::getCurrentUbcTime]
         *     equals some [BlockTime] "A"'s [::getTime], then [A] is returned. Never
         *     returns [null].
         */
        public static BlockTime getCurrentBlockTime(final OffsetTime currentTime) {
            final BlockTime[] blockTimes = values();
            for (int i = 0; i < blockTimes.length - 1; i++) {
                if ( // enter this block if [currentTime] is between the times for [i, i+1):
                        currentTime.compareTo(blockTimes[i].time) >= 0
                        && currentTime.compareTo(blockTimes[i + 1].time) < 0) {
                    return blockTimes[i];
                }
            }
            return OUTSIDE_REGULAR_CLASS_TIME;
        }

        /**
         * @param currentTime The [OffsetTime] to use as the current time for this
         *     operation. Must not be [null].
         * @return The [BlockTime] considered to follow the current block time (see
         *     [getCurrentBlockTime]). If [getCurrentBlockTime] is [null], then this
         *     will return the very first [BlockTime] of the day. If it is the one
         *     directly preceding [END_OF_DAY], then this returns [null]. Never returns
         *     [END_OF_DAY].
         */
        public static BlockTime getNextBlockTime(final OffsetTime currentTime) {
            final BlockTime currentBlock = getCurrentBlockTime(currentTime);
            assert currentBlock != null : "getCurrentBlockTime violated its own spec";
            final BlockTime[] blockTimes = values();

            if (currentBlock == OUTSIDE_REGULAR_CLASS_TIME) {
                // Current time is not a school-hour. Next block is the very first.
                return blockTimes[0];

            } else {
                // Current block is in school hours.
                return blockTimes[currentBlock.ordinal() + 1];
            }
        }

        /**
         * @param attr An [Attr] object. Must not be [null].
         * @return A [BlockTime] whose [getXmlConstantValue] is equal to [attr.getValue].
         * @throws MalformedXmlDataException if no such [BlockTime] can be found.
         */
        public static BlockTime decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            for (final BlockTime blockTime : BlockTime.values()) {
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
