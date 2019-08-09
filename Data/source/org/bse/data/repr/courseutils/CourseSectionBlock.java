package org.bse.data.repr.courseutils;

import org.bse.data.repr.courseutils.CourseUtils.BlockTime;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlParsingUtils;
import org.w3c.dom.Attr;
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
    // TODO [repr][CourseSectionBlock]: add representation for location (building).

    public CourseSectionBlock(Element blockElement) throws MalformedXmlDataException {
        this.isWaitlist = blockElement.getAttributeNode(Xml.OPTIONAL_WAITLIST_FLAG_ATTR.value) != null;

        this.dayOfWeek = null; // TODO [xml:read][CourseSectionBlock]

        this.repetitionType = BlockRepetition.decodeXmlAttr(
                blockElement.getAttributeNode(Xml.OPTIONAL_REPEAT_TYPE_ATTR.value)
        );

        final BlockTime start = BlockTime.decodeXmlAttr(
                XmlParsingUtils.getMandatoryAttr(blockElement, Xml.BEGIN_TIME_ATTR)
        );
        final BlockTime end = BlockTime.decodeXmlAttr(
                XmlParsingUtils.getMandatoryAttr(blockElement, Xml.END_TIME_ATTR)
        );
        this.timeEnclosure = new BlockTimeEnclosure(start, end);
    }

    // *note: do not change visibility. may be used in GUI
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

    public BlockTime getStartTime() {
        return timeEnclosure.begin;
    }

    public BlockTime getEndTime() {
        return timeEnclosure.end;
    }



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
        public final String xmlAttrVal;

        BlockRepetition(String xmlAttrVal) {
            this.xmlAttrVal = xmlAttrVal;
        }

        public boolean mayOverlapWith(BlockRepetition other) {
            return this == other || (this == EVERY_WEEK || other == EVERY_WEEK);
        }

        /**
         * @param attr An Attr object. May be null.
         * @return A [BlockRepetition] whose [xmlAttrVal] is equal to [attr.getValue].
         *     Never returns null. If [attr] is null, returns [EVERY_WEEK] by default.
         * @throws MalformedXmlDataException if no such [BlockRepetition] can be found.
         */
        private static BlockRepetition decodeXmlAttr(Attr attr) throws MalformedXmlDataException {
            if (attr == null) {
                return EVERY_WEEK;
            }
            try {
                return BlockRepetition.valueOf(attr.getValue());
            } catch (IllegalArgumentException e) {
                throw MalformedXmlDataException.invalidAttrVal(attr);
            }
        }
    }



    public enum Xml implements XmlParsingUtils.XmlConstant {
        BLOCK_TAG ("Block"),

        DAY_OF_WEEK_ATTR ("day"),
        BEGIN_TIME_ATTR ("begin"),
        END_TIME_ATTR ("end"),

        // if not present, assumed to be every week. See [BlockRepetition.value] for decode.
        OPTIONAL_REPEAT_TYPE_ATTR ("repeat"),

        // if not present, assumed to be false, otherwise to be true. value ignored.
        OPTIONAL_WAITLIST_FLAG_ATTR ("waitlist"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

}
