package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.courseutils.UbcTimeUtils.BlockTime;
import com.dvf.ucst.core.spider.CourseWip;
import com.dvf.ucst.utils.general.WorkInProgress;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.function.Function;

/**
 * One of several blocks (typically 2 or 3) that describe
 * specific meeting places and times for a [CourseSection].
 * Has no notion of semester context.
 */
public final class CourseSectionBlock {

    private final CourseUtils.WeekDay weekDay;
    private final BlockRepetition repetitionType;
    private final BlockTimeEnclosure timeEnclosure;
    // TODO [repr][CourseSectionBlock]: add representation for location (building).

    CourseSectionBlock(final Element blockElement) throws MalformedXmlDataException {
        this.weekDay = CourseUtils.WeekDay.decodeXmlAttr(XmlUtils
                .getMandatoryAttr(blockElement, Xml.DAY_OF_WEEK_ATTR)
        );
        this.repetitionType = BlockRepetition.decodeXmlAttr(blockElement
                .getAttributeNode(Xml.OPTIONAL_REPEAT_TYPE_ATTR.getXmlConstantValue())
        );
        final BlockTime start = BlockTime.decodeXmlAttr(XmlUtils
                .getMandatoryAttr(blockElement, Xml.BEGIN_TIME_ATTR)
        );
        final BlockTime end = BlockTime.decodeXmlAttr(XmlUtils
                .getMandatoryAttr(blockElement, Xml.END_TIME_ATTR)
        );
        this.timeEnclosure = new BlockTimeEnclosure(start, end);
    }

    // *note: do not change visibility. may be used in GUI.
    public boolean overlapsWith(final CourseSectionBlock other) {
        if (getWeekDay() != other.getWeekDay()) {
            return false;
        } else if (getRepetitionType().mayOverlapWith(other.getRepetitionType())) {
            return this.timeEnclosure.overlapsWith(other.timeEnclosure);
        } else {
            return false;
        }
    }

    public CourseUtils.WeekDay getWeekDay() {
        return weekDay;
    }

    public BlockRepetition getRepetitionType() {
        return repetitionType;
    }

    public BlockTime getBeginTime() {
        return timeEnclosure.begin;
    }

    public BlockTime getEndTime() {
        return timeEnclosure.end;
    }

    public static Element createXmlOfWorkInProgress(
            final Function<XmlUtils.XmlConstant, Element> elementSupplier,
            final CourseWip.CourseSectionWip.CourseSectionBlockWip wip
    ) throws WorkInProgress.IncompleteWipException {
        final Element blockElement = elementSupplier.apply(Xml.BLOCK_TAG);
        blockElement.setAttribute(
                Xml.DAY_OF_WEEK_ATTR.getXmlConstantValue(),
                wip.getWeekDay().getXmlConstantValue()
        );
        blockElement.setAttribute(
                Xml.BEGIN_TIME_ATTR.getXmlConstantValue(),
                wip.getBeginTime().getXmlConstantValue()
        );
        blockElement.setAttribute(
                Xml.END_TIME_ATTR.getXmlConstantValue(),
                wip.getEndTime().getXmlConstantValue()
        );
        if (wip.getRepetitionType() != BlockRepetition.EVERY_WEEK) {
            blockElement.setAttribute(
                    Xml.OPTIONAL_REPEAT_TYPE_ATTR.getXmlConstantValue(),
                    wip.getRepetitionType().getXmlConstantValue()
            );
        }
        return blockElement;
    }



    private static final class BlockTimeEnclosure {

        private final BlockTime begin;
        private final BlockTime end;

        private BlockTimeEnclosure(BlockTime begin, BlockTime end) {
            this.begin = begin;
            this.end = end;
        }

        private boolean overlapsWith(final BlockTimeEnclosure other) {
            return begin.isBefore(other.end) && end.isAfter(other.begin);
        }
    }

    public enum BlockRepetition implements XmlUtils.XmlConstant {
        EVERY_WEEK ("all"), // the default.
        ALTERNATING_FIRST ("2:1"),
        ALTERNATING_SECOND ("2:2"),
        ;
        private final String xmlAttrVal;

        BlockRepetition(String xmlAttrVal) {
            this.xmlAttrVal = xmlAttrVal;
        }

        public boolean mayOverlapWith(BlockRepetition other) {
            return this == other || (this == EVERY_WEEK || other == EVERY_WEEK);
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }

        /**
         * @param attr An [Attr] object. May be [null].
         * @return A [BlockRepetition] whose [xmlAttrVal] is equal to [attr.getValue].
         *     Never returns [null]. If [attr] is [null], returns [EVERY_WEEK] by default.
         * @throws MalformedXmlDataException if no such [BlockRepetition] can be found.
         */
        private static BlockRepetition decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
            if (attr == null) {
                return EVERY_WEEK;
            }
            for (final BlockRepetition blockRepetition : BlockRepetition.values()) {
                if (blockRepetition.getXmlConstantValue().equals(attr.getValue())) {
                    return blockRepetition;
                }
            }
            throw MalformedXmlDataException.invalidAttrVal(attr);
        }
    }



    public enum Xml implements XmlUtils.XmlConstant {
        BLOCK_TAG ("Block"),

        DAY_OF_WEEK_ATTR ("day"),
        BEGIN_TIME_ATTR ("begin"),
        END_TIME_ATTR ("end"),

        // if not present, assumed to be every week. See [BlockRepetition.value] for decode.
        OPTIONAL_REPEAT_TYPE_ATTR ("repeat"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
