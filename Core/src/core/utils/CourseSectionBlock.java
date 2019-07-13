package core.utils;

import java.time.DayOfWeek;

/**
 * One of around one to three blocks that describe specific
 * meeting places and times for a CourseSection.
 */
public class CourseSectionBlock {

    private final DayOfWeek dayOfWeek;
    private final BlockTimeEnclosure timeEnclosure;

    public CourseSectionBlock(DayOfWeek dayOfWeek, BlockTimeEnclosure timeEnclosure) {
        this.dayOfWeek = dayOfWeek;
        this.timeEnclosure = timeEnclosure;
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

        public BlockTimeEnclosure(String begin, String end) {
            this.begin = new BlockTime(begin);
            this.end = new BlockTime(end);
        }

        public boolean overlapsWith(BlockTimeEnclosure other) {
            return begin.isBefore(other.end) && end.isAfter(other.begin);
        }

        /**
         * TODO: discuss whether this is an appropriate representation for our use case.
         */
        private static final class BlockTime {

            private final int value;

            public BlockTime(int value) {
                this.value = value;
            }

            /**
             * TODO: write documentation on expected format.
             * TODO: consider adding a throws to declaration.
             * @param valueString in the format like 3:00
             */
            public BlockTime(String valueString) {
                this.value = 0; // TODO: decode
            }

            public boolean isBefore(BlockTime other) {
                return value < other.value;
            }

            public boolean isAfter(BlockTime other) {
                return value > other.value;
            }
        }
    }
}
