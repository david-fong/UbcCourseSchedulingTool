package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.courseutils.CourseUtils.Semester;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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

}