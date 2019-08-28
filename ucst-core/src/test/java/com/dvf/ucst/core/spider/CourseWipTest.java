package com.dvf.ucst.core.spider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseWipTest {

    @Test
    void makeCourseWip() {
        final CourseWip courseWip = new CourseWip();
        courseWip.setCourseIdToken("221");
    }

}