package com.dvf.ucst.core.courseutils;

import com.dvf.ucst.core.courseutils.CourseUtils.Semester;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Map;

import static com.dvf.ucst.core.courseutils.CourseUtils.Semester.*;
import static com.dvf.ucst.core.courseutils.CourseUtils.WeekDay.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseUtilsTest {

    @Test
    void semesterGetCurrent() {
        final Map<ZonedDateTime, Semester> datesToExpectedMap = Map.of(
                SUMMER_S1.getApproxClassStartDay(2019, MONDAY), SUMMER_S1,
                SUMMER_S1.getApproxClassStartDay(2019, TUESDAY), SUMMER_S1,
                SUMMER_S1.getApproxClassStartDay(2019, MONDAY).minusDays(1), WINTER_S2
        );
        datesToExpectedMap.forEach((
                final ZonedDateTime zonedDateTime,
                final Semester expectedCurrentSemester
        ) -> assertEquals(
                expectedCurrentSemester,
                getCurrentSemester(zonedDateTime)
        ));
    }

}