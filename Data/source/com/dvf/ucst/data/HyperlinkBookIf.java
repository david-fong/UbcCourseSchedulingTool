package com.dvf.ucst.data;

import com.dvf.ucst.data.courseutils.Course;
import com.dvf.ucst.data.faculties.FacultyTreeNode;
import com.dvf.ucst.data.faculties.FacultyTreeRootCampus;

/**
 * https://courses.students.ubc.ca/cs/courseschedule
 */
public interface HyperlinkBookIf {

    String REGISTRATION_HOME = "https://courses.students.ubc.ca/cs/courseschedule";
    String TNAME_QUERY_TOKEN_STUB = "&tname=";

    String getRegistrationSiteUrl();

    //String getUbcLink();

    //String getDedicatedSiteLink();



    enum PNames {
        BROWSE_COURSES("subjarea"),
        ;
        private final String registrationSiteUrl;

        PNames(String pName) {
            this.registrationSiteUrl = REGISTRATION_HOME + "?pname=" + pName;
        }
    }

    // pname=subjarea
    enum RegistrationSubjAreaQuery {
        CAMPUS ("campuscd", "subj-all-departments"),
        FACULTY ("dept", "subj-department"),
        COURSE ("course", "subj-course"),
        SECTION ("section", "subj-section")
        ;
        public final String tokenStub;
        public final String tnameQuery;

        RegistrationSubjAreaQuery(String name, String tnameQueryVal) {
            this.tokenStub = String.format("&%s=", name);
            this.tnameQuery = TNAME_QUERY_TOKEN_STUB + tnameQueryVal;
        }

        public static String getUrl(final FacultyTreeRootCampus campus) {
            return PNames.BROWSE_COURSES.registrationSiteUrl
                    + CAMPUS.tnameQuery
                    + CAMPUS.tokenStub + campus.getAbbreviation();
        }

        public static String getUrl(final FacultyTreeNode faculty) {
            return PNames.BROWSE_COURSES.registrationSiteUrl
                    + FACULTY.tnameQuery
                    + FACULTY.tokenStub + faculty.getAbbreviation();
        }

        public static String getUrl(final Course course) {
            return PNames.BROWSE_COURSES.registrationSiteUrl
                    + COURSE.tnameQuery
                    + COURSE.tokenStub + course.getCourseIdToken();
        }

        public static String getUrl(final Course.CourseSection section) {
            return PNames.BROWSE_COURSES.registrationSiteUrl
                    + SECTION.tnameQuery
                    + COURSE.tokenStub + section.getParentCourse().getCourseIdToken()
                    + SECTION.tokenStub + section.getSectionIdToken();
        }
    }

}
