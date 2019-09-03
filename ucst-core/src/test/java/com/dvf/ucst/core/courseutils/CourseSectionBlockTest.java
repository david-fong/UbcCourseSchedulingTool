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
        void assertOverlaps() {
            final Map<BlockTimeEnclosure, Set<BlockTimeEnclosure>> overlapsWithMap;
            try {
                overlapsWithMap = Map.of(
                        bte(T0800, T0900), Set.of(bte(T0800, T0900), bte(T0830, T0900), bte(T0800, T0930), bte(T0830, T2130))
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