package org.bse.data.repr.courseutils;

import org.bse.data.repr.courseutils.CourseSectionCategory.CourseSection;
import org.bse.data.repr.courseutils.CourseUtils.BlockTime;
import org.w3c.dom.Element;

import java.time.DayOfWeek;

/**
 * One of several blocks (typically 2 or 3) that describe
 * specific meeting places and times for a [CourseSection].
 * Has no notion of semester context.
 */
public final class CourseSectionBlock {

    private final CourseSection parentSection;
    private final boolean isWaitlist;

    private final DayOfWeek dayOfWeek;
    private final BlockRepetition repetitionType;
    private final BlockTimeEnclosure timeEnclosure;

    public static CourseSectionBlock fromXml(Element blockElement) {
        // note: for parsing enumConstant.name(), use enumClass.valueOf(String name).
        return null; // TODO:
    }

    private CourseSectionBlock(CourseSection parentSection, boolean isWaitlist,
                              DayOfWeek dayOfWeek, BlockRepetition repetitionType,
                              BlockTimeEnclosure timeEnclosure) {
        this.parentSection = parentSection;
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
    public static final class BlockTimeEnclosure {

        private final BlockTime begin;
        private final BlockTime end;

        public BlockTimeEnclosure(BlockTime begin, BlockTime end) {
            this.begin = begin;
            this.end = end;
        }

        public boolean overlapsWith(BlockTimeEnclosure other) {
            return begin.isBefore(other.end) && end.isAfter(other.begin);
        }
    }

    public enum BlockRepetition {
        EVERY_WEEK,
        ALTERNATING_FIRST,
        ALTERNATING_SECOND,
        ;

        public boolean mayOverlapWith(BlockRepetition other) {
            return this == other || (this == EVERY_WEEK || other == EVERY_WEEK);
        }
    }

}
