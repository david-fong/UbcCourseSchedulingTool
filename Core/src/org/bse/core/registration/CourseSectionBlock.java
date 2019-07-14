package org.bse.core.registration;

import org.bse.core.registration.CourseUtils.Semester;
import org.bse.core.registration.CourseUtils.BlockRepetition;
import org.bse.core.registration.CourseUtils.BlockTime;

import java.time.DayOfWeek;

/**
 * One of several blocks (typically 2 or 3) that describe
 * specific meeting places and times for a [CourseSection].
 */
public class CourseSectionBlock {

    private final CourseSection parentSection;
    private final boolean isWaitlist;

    private final Semester semester;
    private final DayOfWeek dayOfWeek;
    private final BlockRepetition repetitionType;
    private final BlockTimeEnclosure timeEnclosure;

    public CourseSectionBlock(CourseSection parentSection, boolean isWaitlist,
                              Semester semester, DayOfWeek dayOfWeek,
                              BlockRepetition repetitionType,
                              BlockTimeEnclosure timeEnclosure) {
        this.parentSection = parentSection;
        this.isWaitlist = isWaitlist;
        this.semester = semester;
        this.dayOfWeek = dayOfWeek;
        this.repetitionType = repetitionType;
        this.timeEnclosure = timeEnclosure;
    }

    // TODO: Add JSON constructor:

    public CourseSection getParentSection() {
        return parentSection;
    }
    public boolean getIsWaitlist() {
        return isWaitlist;
    }
    public Semester getSemester() {
        return semester;
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

}
