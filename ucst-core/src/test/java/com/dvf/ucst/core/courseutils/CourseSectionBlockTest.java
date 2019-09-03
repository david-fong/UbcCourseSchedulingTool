package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.courseutils.CourseSectionBlock.BlockTimeEnclosure;
import com.dvf.ucst.core.courseutils.CourseSectionBlock.IllegalTimeEnclosureException;
import com.dvf.ucst.core.courseutils.UbcTimeUtils.BlockTime;
import com.dvf.ucst.core.spider.CourseWip.CourseSectionWip.CourseSectionBlockWip;
import com.dvf.ucst.utils.general.WorkInProgress;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlIoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.util.*;
import java.util.function.BiPredicate;

import static com.dvf.ucst.core.courseutils.UbcTimeUtils.BlockTime.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class CourseSectionBlockTest {

    private static final Map<BlockTimeEnclosure, Set<BlockTimeEnclosure>> OVERLAPS_WITH_BLOCK_TIME_MAP;
    static {
        try {
            OVERLAPS_WITH_BLOCK_TIME_MAP = Map.of( // ----- || ?? description -------------------------------
                    // key = short duration at beginning of day:
                    bte(T0800, T0900), Set.of(
                            bte(T0800, T0900), // == same begin and end
                            bte(T0830, T0900), // >= nudge begin later, same end
                            bte(T0800, T0930), // => same begin, nudge end later
                            bte(T0830, T2130)  // >> nudge begin later, shove end much later
                    ),
                    // key = medium duration during common hours:
                    bte(T1030, T1600), Set.of(
                            bte(T1000, T1100), // << nudge begin earlier, end close to key.begin
                            bte(T0800, T1100), // << push begin earlier, end close to key.begin
                            bte(T0800, T1530), // << push begin earlier, nudge end earlier
                            bte(T1100, T1530), // >< nudge bounds inward
                            bte(T1300, T1330), // >< nudge bounds inward more
                            bte(T1000, T1630), // <> nudge bounds outward
                            bte(T0830, T2000)  // <> nudge bounds outward more
                    ),
                    // key = short duration during later hours:
                    bte(T1530, T1700), Set.of(
                            bte(T1530, T1700), // == same begin and end
                            bte(T1600, T1630), // >< nudge bounds inward
                            bte(T1600, T1700), // >= nudge begin later, same end
                            bte(T1530, T1630), // =< same begin, nudge end earlier
                            bte(T1500, T1700)  // <= nudge begin earlier, same end
                    )
            );
        } catch (IllegalTimeEnclosureException e) {
            throw new RuntimeException("you weren't supposed to do that lol");
        }
    }

    private static final Map<BlockTimeEnclosure, Set<BlockTimeEnclosure>> NO_OVERLAPS_WITH_BLOCK_TIME_MAP;
    static {
        try {
            NO_OVERLAPS_WITH_BLOCK_TIME_MAP = Map.of( // --- || ??
                    // key = short duration at beginning of day:
                    bte(T1000, T1130), Set.of(
                            bte(T0800, T0900), // <<
                            bte(T0830, T1000), // <=
                            bte(T1130, T1200), // =>
                            bte(T1300, T1930)  // >>
                    ),
                    // key = medium duration during common hours:
                    bte(T1230, T1630), Set.of(
                            bte(T1000, T1200), // <<
                            bte(T0800, T1230), // <=
                            bte(T1630, T1700), // =>
                            bte(T1700, T1830)  // >>
                    ),
                    // key = short duration during later hours:
                    bte(T1530, T1800), Set.of(
                            bte(T1130, T1400), // <<
                            bte(T1430, T1530), // <=
                            bte(T1800, T2000), // =>
                            bte(T1830, T2030)  // >>
                    )
            );
        } catch (IllegalTimeEnclosureException e) {
            throw new RuntimeException("you weren't supposed to do that lol");
        }
    }

    @Test
    void testOverlaps() {
        final Document doc = XmlIoUtils.createNewXmlDocument();
        final BiPredicate<CourseSectionBlockWip, CourseSectionBlockWip> checkBlocksOverlap = (b1, b2) -> {
            return Objects.requireNonNull(createBlockObject(doc, b1))
                    .overlapsWith(Objects.requireNonNull(createBlockObject(doc, b2)));
        };
        OVERLAPS_WITH_BLOCK_TIME_MAP.forEach((
                final BlockTimeEnclosure timeEnclosure,
                final Set<BlockTimeEnclosure> overlappingEnclosures
        ) -> overlappingEnclosures.forEach((
                final BlockTimeEnclosure overlappingEnclosure
        ) -> {
            final CourseSectionBlockWip blockWip1 = new CourseSectionBlockWip()
                    .setBeginTime(timeEnclosure.getBegin())
                    .setEndTime(timeEnclosure.getEnd())
                    ;
            final CourseSectionBlockWip blockWip2 = new CourseSectionBlockWip()
                    .setBeginTime(overlappingEnclosure.getBegin())
                    .setEndTime(overlappingEnclosure.getEnd())
                    ;

            // WeekDay combinations:
            List.of(CourseUtils.WeekDay.values()).forEach((
                    final CourseUtils.WeekDay weekDay1
            ) -> List.of(CourseUtils.WeekDay.values()).forEach((
                    final CourseUtils.WeekDay weekDay2
            ) -> {
                blockWip1.setWeekDay(weekDay1);
                blockWip2.setWeekDay(weekDay2);

                // BlockRepetition combinations:
                List.of(CourseSectionBlock.BlockRepetition.values()).forEach((
                        final CourseSectionBlock.BlockRepetition rep1
                ) -> List.of(CourseSectionBlock.BlockRepetition.values()).forEach((
                        final CourseSectionBlock.BlockRepetition rep2
                ) -> {
                    blockWip1.setRepetitionType(rep1);
                    blockWip2.setRepetitionType(rep2);
                    if (weekDay1 == weekDay2 && rep1.mayOverlapWith(rep2)) {
                        assertTrue(checkBlocksOverlap.test(blockWip1, blockWip2));
                    } else {
                        assertFalse(checkBlocksOverlap.test(blockWip1, blockWip2));
                    }
                }));
            }));
        }));
    }

    private CourseSectionBlock createBlockObject(final Document doc, final CourseSectionBlockWip blockWip) {
        try {
            //XmlIoUtils.printNode(createBlockElement(doc, blockWip));
            return new CourseSectionBlock(createBlockElement(doc, blockWip));
        } catch (final MalformedXmlDataException e) {
            fail();
            return null;
        }
    }

    // does not add element to doc. blockWip must be complete.
    private Element createBlockElement(final Document doc, final CourseSectionBlockWip blockWip) {
        try {
            return CourseSectionBlock.createXmlOfWorkInProgress(
                    tagName -> doc.createElement(tagName.getXmlConstantValue()),
                    blockWip
            );
        } catch (WorkInProgress.IncompleteWipException | IllegalTimeEnclosureException e) {
            fail("Encountered unexpected " + e.getClass() + " exception");
            return null;
        }
    }



    /**
     *
     */
    @Nested
    final class CourseSectionBlockWipTest {

        @Test
        void makeWip() {
            final CourseSectionBlockWip blockWip = new CourseSectionBlockWip()
                    .setBeginTime(T0800)
                    .setEndTime(T0900)
                    .setRepetitionType(CourseSectionBlock.BlockRepetition.EVERY_WEEK)
                    .setWeekDay(CourseUtils.WeekDay.MONDAY);
            final Set<ThrowingSupplier<Object>> getters = Set.of(
                    blockWip::getBeginTime,
                    blockWip::getEndTime,
                    blockWip::getRepetitionType,
                    blockWip::getWeekDay
            );
            getters.forEach(Assertions::assertDoesNotThrow);
        }

        @Test
        void assertThrowsIncomplete() {
            // make a block wip and don't populate its fields:
            final CourseSectionBlockWip blockWip = new CourseSectionBlockWip();
            final Set<ThrowingSupplier<Object>> getters = Set.of(
                    blockWip::getBeginTime,
                    blockWip::getEndTime,
                    blockWip::getRepetitionType,
                    blockWip::getWeekDay
            );
            getters.forEach(getter -> assertThrows(
                    WorkInProgress.IncompleteWipException.class,
                    getter::get
            ));
        }

        // volatile test. compared string literal subject to change with xml spec.
        @Test
        void toXml() {
            final Document doc = XmlIoUtils.createNewXmlDocument();
            doc.appendChild(createBlockElement(doc, new CourseSectionBlockWip()
                    .setBeginTime(T0800)
                    .setEndTime(T0900)
                    .setRepetitionType(CourseSectionBlock.BlockRepetition.EVERY_WEEK)
                    .setWeekDay(CourseUtils.WeekDay.MONDAY)
            ));
            try {
                assertEquals(
                        "<Block begin=\"08:00\" day=\"mon\" end=\"09:00\"/>", // "\r\n"
                        XmlIoUtils.printNodeToString(doc).trim()
                );
            } catch (final TransformerException e) {
                fail("Unexpected error serializing document to string");
            }
        }
    }



    /**
     * Tests for the private class [BlockTimeEnclosure]
     */
    @Nested
    final class BlockTimeEnclosureTest {

        @Test
        void assertNoOverlaps() {
            NO_OVERLAPS_WITH_BLOCK_TIME_MAP.forEach((enclosure, nonOverlappingEnclosures) -> {
                nonOverlappingEnclosures.forEach(nonOverlappingEnclosure -> {
                    assertFalse(enclosure.overlapsWith(nonOverlappingEnclosure));
                    assertFalse(nonOverlappingEnclosure.overlapsWith(enclosure)); // and vice-versa
                });
            });
        }

        @Test
        void assertOverlaps() {
            OVERLAPS_WITH_BLOCK_TIME_MAP.forEach((enclosure, overlappingEnclosures) -> {
                overlappingEnclosures.forEach(overlappingEnclosure -> {
                    assertTrue(enclosure.overlapsWith(overlappingEnclosure));
                    assertTrue(overlappingEnclosure.overlapsWith(enclosure)); // and vice-versa
                });
            });
        }

        @Test
        void assertIllegalEnclosure() {
            final List<BlockTime> earlierTimes = new ArrayList<>();
            for (final BlockTime blockTime : BlockTime.values()) {
                // check each possible zero-duration time enclosure:
                assertThrows(IllegalTimeEnclosureException.class, () -> bte(blockTime, blockTime));

                for (final BlockTime earlierTime : earlierTimes) {
                    assertThrows(IllegalTimeEnclosureException.class, () -> bte(blockTime, earlierTime));
                }
                earlierTimes.add(blockTime);
            }
        }
    }

    // for sake of brevity:
    private static BlockTimeEnclosure bte(
            final BlockTime begin,
            final BlockTime end
    ) throws IllegalTimeEnclosureException {
        return BlockTimeEnclosure.of(begin, end);
    }

}