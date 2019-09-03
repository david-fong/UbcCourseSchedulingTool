package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.courseutils.CourseSectionBlock.BlockTimeEnclosure;
import com.dvf.ucst.core.courseutils.CourseSectionBlock.IllegalTimeEnclosureException;
import com.dvf.ucst.core.courseutils.UbcTimeUtils.BlockTime;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dvf.ucst.core.courseutils.UbcTimeUtils.BlockTime.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseSectionBlockTest {

    /**
     * Tests for the private class [BlockTimeEnclosure]
     */
    static final class BlockTimeEnclosureTest {

        @Test
        void assertNoOverlaps() {
            final Map<BlockTimeEnclosure, Set<BlockTimeEnclosure>> noOverlapsWithMap;
            try {
                noOverlapsWithMap = Map.of( // --- || ??
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
            noOverlapsWithMap.forEach((enclosure, nonOverlappingEnclosures) -> {
                nonOverlappingEnclosures.forEach(nonOverlappingEnclosure -> {
                    assertFalse(enclosure.overlapsWith(nonOverlappingEnclosure));
                    assertFalse(nonOverlappingEnclosure.overlapsWith(enclosure)); // and vice-versa
                });
            });
        }

        @Test
        void assertOverlaps() {
            final Map<BlockTimeEnclosure, Set<BlockTimeEnclosure>> overlapsWithMap;
            try {
                overlapsWithMap = Map.of( // ----- || ?? description -------------------------------
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
            overlapsWithMap.forEach((enclosure, overlappingEnclosures) -> {
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

        // for sake of brevity:
        private static BlockTimeEnclosure bte(
                final BlockTime begin,
                final BlockTime end
        ) throws IllegalTimeEnclosureException {
            return BlockTimeEnclosure.of(begin, end);
        }
    }

}