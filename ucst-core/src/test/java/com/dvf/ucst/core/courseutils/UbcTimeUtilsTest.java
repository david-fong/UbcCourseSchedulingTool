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

    @Test
    void timeSandbox() {
        System.out.println("Timezone ID: " + getUbcTimezoneOffset());
        System.out.println("Timezone ID: " + ZoneId.of(UBC_TIMEZONE_ID_STRING));
        final ZoneId hkId = ZoneId.of("Hongkong");
        System.out.println(ZonedDateTime.of(LocalDateTime.now(), hkId)); // not the behaviour we want.
        System.out.println(LocalDateTime.now().atZone(hkId)); // also not the behaviour we want.
        System.out.println(ZonedDateTime.now(hkId)); // <- this is it, chief.
        System.out.println(ZonedDateTime.now(hkId).toOffsetDateTime());
        System.out.println(ZonedDateTime.now(getUbcTimezoneOffset()).toOffsetDateTime().toOffsetTime()); // <- use this for times.
        System.out.println(ZonedDateTime.now(ZoneId.of(UBC_TIMEZONE_ID_STRING)).toOffsetDateTime().toOffsetTime()); // same as above. don't use, though.
    }

    @Test
    void currentBlockTime() {
        final OffsetTime preFirstBlockTime = BlockTime.values()[0].getTime().minusSeconds(1);
        final Map<LocalTime, BlockTime> timeToExpectedBlockMap = Map.of(
                LocalTime.of( 5, 26), OUTSIDE,
                LocalTime.of(preFirstBlockTime.getHour(), preFirstBlockTime.getMinute()), OUTSIDE,
                LocalTime.of( 8,  0), T0800,
                LocalTime.of( 8,  1), T0800,
                LocalTime.of( 8, 59), T0830,
                LocalTime.of( 9,  0), T0900,
                LocalTime.of(12, 17), T1200,
                LocalTime.of(12, 30), T1230,
                LocalTime.of(17, 42), T1730,
                LocalTime.of(OUTSIDE.getTime().getHour(), OUTSIDE.getTime().getMinute()), OUTSIDE
        );
        for (final Map.Entry<LocalTime, BlockTime> timeToExpectedBlockEntry : timeToExpectedBlockMap.entrySet()) {
            final OffsetTime inputTime = timeToExpectedBlockEntry.getKey().atOffset(getUbcTimezoneOffset());
            assertEquals(getCurrentBlockTime(inputTime), timeToExpectedBlockEntry.getValue());
        }
    }

}