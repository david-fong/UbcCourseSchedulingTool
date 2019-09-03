package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.courseutils.UbcTimeUtils.BlockTime;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Map;

import static com.dvf.ucst.core.courseutils.UbcTimeUtils.*;
import static com.dvf.ucst.core.courseutils.UbcTimeUtils.BlockTime.*;
import static org.junit.jupiter.api.Assertions.*;

class UbcTimeUtilsTest {

    private static final BlockTime OUTSIDE = OUTSIDE_REGULAR_CLASS_TIME; // for sake of brevity.
    private static final BlockTime FIRST_BLOCK = BlockTime.values()[0];
    private static final BlockTime LAST_BLOCK = BlockTime.values()[OUTSIDE.ordinal() - 1];

    @Test
    void timeSandbox() {
        System.out.println("Timezone ID: " + getUbcTimezoneOffset());
        System.out.println("Timezone ID: " + UBC_TIMEZONE_ZONE_ID);
        final ZoneId hkId = ZoneId.of("Hongkong");
        System.out.println(hkId.getId());
        System.out.println(ZonedDateTime.of(LocalDateTime.now(), hkId)); // not the behaviour we want.
        System.out.println(LocalDateTime.now().atZone(hkId)); // also not the behaviour we want.
        System.out.println(ZonedDateTime.now(hkId)); // <- this is it, chief.
        System.out.println(ZonedDateTime.now(hkId).toOffsetDateTime());
        System.out.println(ZonedDateTime.now(hkId).withZoneSameInstant(ZoneId.systemDefault())); // this is how to convert between timezones
        System.out.println(ZonedDateTime.now(getUbcTimezoneOffset()).toOffsetDateTime().toOffsetTime()); // <- use this for times.
        System.out.println(ZonedDateTime.now(UBC_TIMEZONE_ZONE_ID).toOffsetDateTime().toOffsetTime()); // same as above. don't use, though.
    }

    @Test
    void currentBlockTime() {
        final OffsetTime preFirstBlockTime = FIRST_BLOCK.getTime().minusNanos(1);
        final OffsetTime preEndOfDay = OUTSIDE_REGULAR_CLASS_TIME.getTime().minusNanos(1);
        final Map<LocalTime, BlockTime> timeToExpectedBlockMap = Map.of(
                LocalTime.of( 5, 26), OUTSIDE,
                LocalTime.of(preFirstBlockTime.getHour(), preFirstBlockTime.getMinute()), OUTSIDE,
                LocalTime.of( 8,  0), T0800,
                LocalTime.of( 8,  1), T0800,
                LocalTime.of( 8, 59), T0830,
                LocalTime.of(12, 17), T1200,
                LocalTime.of(12, 30), T1230,
                LocalTime.of(17, 42), T1730,
                LocalTime.of(preEndOfDay.getHour(), preEndOfDay.getMinute()), LAST_BLOCK,
                LocalTime.of(OUTSIDE.getTime().getHour(), OUTSIDE.getTime().getMinute()), OUTSIDE
        );
        timeToExpectedBlockMap.forEach((
                final LocalTime localTime,
                final BlockTime blockTime
        ) -> assertEquals(
                blockTime,
                getCurrentBlockTime(localTime.atOffset(getUbcTimezoneOffset())),
                "given time: " + localTime
        ));
    }

    @Test
    void nextBlockTime() {
        final OffsetTime preFirstBlockTime = BlockTime.values()[0].getTime().minusNanos(1);
        final OffsetTime preEndOfDay = OUTSIDE_REGULAR_CLASS_TIME.getTime().minusNanos(1);
        final Map<LocalTime, BlockTime> timeToExpectedBlockMap = Map.of(
                LocalTime.of( 5, 26), FIRST_BLOCK,
                LocalTime.of(preFirstBlockTime.getHour(), preFirstBlockTime.getMinute()), FIRST_BLOCK,
                LocalTime.of( 8,  0), T0830,
                LocalTime.of( 8,  1), T0830,
                LocalTime.of( 8, 59), T0900,
                LocalTime.of(12, 17), T1230,
                LocalTime.of(12, 30), T1300,
                LocalTime.of(17, 42), T1800,
                LocalTime.of(preEndOfDay.getHour(), preEndOfDay.getMinute()), OUTSIDE,
                LocalTime.of(OUTSIDE.getTime().getHour(), OUTSIDE.getTime().getMinute()), FIRST_BLOCK
        );
        timeToExpectedBlockMap.forEach((
                final LocalTime localTime,
                final BlockTime expectedNextBlock
        ) -> assertEquals(
                expectedNextBlock,
                getNextBlockTime(localTime.atOffset(getUbcTimezoneOffset())),
                "given time: " + localTime
        ));
    }

    @Test
    void _12hrTimeString() {
        final Map<BlockTime, String> blockToExpected12hrStringMap = Map.of(
                T0800,  "8:00 am",
                T0830,  "8:30 am",
                T0900,  "9:00 am",
                T0930,  "9:30 am",
                T1200, "12:00 pm",
                T1230, "12:30 pm",
                T1300,  "1:00 pm",
                T1730,  "5:30 pm",
                T2100,  "9:00 pm",
                T2130,  "9:30 pm"
        );
        for (final Map.Entry<BlockTime, String> blockToExpected12HrStringEntry : blockToExpected12hrStringMap.entrySet()) {
            assertEquals(
                    blockToExpected12HrStringEntry.getValue(),
                    blockToExpected12HrStringEntry.getKey().get12HourTimeString()
            );
        }
    }

    @Test
    void _24hrTimeString() {
        final Map<BlockTime, String> blockToExpected24hrStringMap = Map.of(
                T0800, "08:00",
                T0830, "08:30",
                T0900, "09:00",
                T0930, "09:30",
                T1200, "12:00",
                T1230, "12:30",
                T1300, "13:00",
                T1730, "17:30",
                T2100, "21:00",
                T2130, "21:30"
        );
        for (final Map.Entry<BlockTime, String> blockToExpected24hrStringEntry : blockToExpected24hrStringMap.entrySet()) {
            assertEquals(
                    blockToExpected24hrStringEntry.getValue(),
                    blockToExpected24hrStringEntry.getKey().get24HrTimeString()
            );
        }
    }

}