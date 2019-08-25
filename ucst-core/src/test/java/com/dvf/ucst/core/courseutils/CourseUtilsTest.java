package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.courseutils.CourseUtils.Semester;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.dvf.ucst.core.courseutils.CourseUtils.Semester.*;
import static com.dvf.ucst.core.courseutils.CourseUtils.WeekDay.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseUtilsTest {

    @Test
    void semesterGetCurrent() {
        final Map<LocalDate, Semester> datesToExpectedMap = Map.of(
                SUMMER_S1.getApproxClassStartDay(2019, MONDAY), SUMMER_S1,
                SUMMER_S1.getApproxClassStartDay(2019, TUESDAY), SUMMER_S1,
                SUMMER_S1.getApproxClassStartDay(2019, MONDAY).minus(1, ChronoUnit.DAYS), WINTER_S2
        );
        for (final Map.Entry<LocalDate, Semester> dateToExpectedEntry : datesToExpectedMap.entrySet()) {
            assertEquals(
                    getCurrentSemester(dateToExpectedEntry.getKey()),
                    dateToExpectedEntry.getValue()
            );
        }
    }

    @Test
    void blockTimeAndTimezoneTest() {
        System.out.println("Timezone ID:     " + CourseUtils.UBC_TIMEZONE_ID);
        final ZoneId hkId = ZoneId.of("Hongkong");
        System.out.println(ZonedDateTime.of(LocalDateTime.now(), hkId)); // not the behaviour we want.
        System.out.println(LocalDateTime.now().atZone(hkId)); // also not the behaviour we want.
        System.out.println(ZonedDateTime.now(hkId)); // <- this is it, chief.
        System.out.println(ZonedDateTime.now(hkId).toOffsetDateTime());
        System.out.println(ZonedDateTime.now(CourseUtils.UBC_TIMEZONE_ID).toOffsetDateTime().toOffsetTime()); // <- use this for times.
    }

}