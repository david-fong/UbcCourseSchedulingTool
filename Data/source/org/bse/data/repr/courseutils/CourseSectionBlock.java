package org.bse.data.repr.courseutils;

import org.bse.data.repr.courseutils.CourseUtils.BlockTime;
import org.w3c.dom.Element;

import java.time.DayOfWeek;

/**
 * One of several blocks (typically 2 or 3) that describe
 * specific meeting places and times for a [CourseSection].
 * Has no notion of semester context.
 */
public final class CourseSectionBlock {

    private final boolean isWaitlist;
    private final DayOfWeek dayOfWeek;
    private final BlockRepetition repetitionType;
    private final BlockTimeEnclosure timeEnclosure;

    // TODO [xml:read][CourseSectionBlock]
    public static CourseSectionBlock fromXml(Element blockElement) {
        // note: for parsing enumConstant.name(), use enumClass.valueOf(String name).
        return null; // TODO:
    }

    private CourseSectionBlock(boolean isWaitlist,
                               DayOfWeek dayOfWeek,
                               BlockRepetition repetitionType,
                               BlockTimeEnclosure timeEnclosure) {
        this.isWaitlist = isWaitlist;
        this.dayOfWeek = dayOfWeek;
        this.repetitionType = repetitionType;
        this.timeEnclosure = timeEnclosure;
    }

    public boolean overlapsWith(CourseSectionBlock other) {
        if (dayOfWeek != other.dayOfWeek) {
            return false;
        } else if (repetitionType.mayOverlapWith(other.repetitionType)) {
            return this.timeEnclosure.overlapsWith(other.timeEnclosure);
        } else {
            return false;
        }
    }

    public boolean isWaitlist() {
        return isWaitlist;
    }
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    public BlockRepetition getRepetitionType() {
        return repetitionType;
    }
    public BlockTimeEnclosure getTimeEnclosure() {
        return timeEnclosure;
    }



    /**
     * TODO: write documentation.
     */
    private static final class BlockTimeEnclosure {

        private final BlockTime begin;
        private final BlockTime end;

        private BlockTimeEnclosure(BlockTime begin, BlockTime end) {
            this.begin = begin;
            this.end = end;
        }

        private boolean overlapsWith(BlockTimeEnclosure other) {
            return begin.isBefore(other.end) && end.isAfter(other.begin);
        }
    }

    public enum BlockRepetition {
        EVERY_WEEK ("all"), // the default.
        ALTERNATING_FIRST ("2:1"),
        ALTERNATING_SECOND ("2:2"),
        ;
        public final String value;

        BlockRepetition(String value) {
            this.value = value;
        }

        public boolean mayOverlapWith(BlockRepetition other) {
            return this == other || (this == EVERY_WEEK || other == EVERY_WEEK);
        }
    }



    public enum Xml {
        BLOCK_TAG ("Block"),

        DAY_OF_WEEK_ATTR ("day"),
        BEGIN_TIME_ATTR ("begin"),
        END_TIME_ATTR ("end"),

        // if not present, assumed to be every week. See [BlockRepetition.value] for decode.
        OPTIONAL_REPEAT_TYPE_ATTR ("repeat"),

        // if not present, assumed to be false, otherwise to be true. value ignored.
        OPTIONAL_WAITLIST_FLAG_ATTR ("waitlist"),

        ;
        public final String value;

        Xml(String value) {
            this.value = value;
        }
    }

}
