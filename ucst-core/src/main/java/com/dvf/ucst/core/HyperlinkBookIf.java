package com.dvf.ucst.core;

import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.core.faculties.FacultyTreeRootCampus;

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

        String getRegistrationSiteUrl() {
            return registrationSiteUrl;
        }
    }

    // "...&pname=subjarea..."
    enum RegistrationSubjAreaQuery {
        CAMPUS ("campuscd", "subj-all-departments"),
        FACULTY ("dept", "subj-department"),
        COURSE ("course", "subj-course"),
        SECTION ("section", "subj-section")
        ;
        private final String queryTokenStub;
        private final String tnameQuery;

        RegistrationSubjAreaQuery(final String name, final String tnameQueryVal) {
            this.queryTokenStub = String.format("&%s=", name);
            this.tnameQuery = TNAME_QUERY_TOKEN_STUB + tnameQueryVal;
        }

        String makeQueryToken(final String value) {
            return queryTokenStub + value;
        }

        String getTnameQuery() {
            return tnameQuery;
        }

        public static String getCampusUrl(final FacultyTreeRootCampus campus) {
            return PNames.BROWSE_COURSES.getRegistrationSiteUrl()
                    + CAMPUS.getTnameQuery()
                    + CAMPUS.makeQueryToken(campus.getAbbreviation());
        }

        public static String getFacultyUrl(final FacultyTreeNode faculty) {
            return deepenCampusUrlToFacultyUrl(faculty.getRegistrationSiteUrl(), faculty.getAbbreviation());
        }

        public static String getCourseUrl(final Course course) {
            return deepenFacultyUrlToCourseUrl(
                    course.getFacultyTreeNode().getRegistrationSiteUrl(),
                    course.getCourseIdToken()
            );
        }

        public static String getSectionUrl(final Course.CourseSection section) {
            return deepenCourseUrlToSectionUrl(
                    section.getParentCourse().getRegistrationSiteUrl(),
                    section.getSectionIdToken()
            );
        }

        public static String deepenCampusUrlToFacultyUrl(final String campusUrl, final String facultyIdToken) {
            return campusUrl.replaceFirst(CAMPUS.getTnameQuery(), FACULTY.getTnameQuery())
                    + FACULTY.makeQueryToken(facultyIdToken);
        }

        /*
        METHODS FOR SPIDERS: since [Course] and [CourseSection] objects don't exist yet for them:
         */

        public static String deepenFacultyUrlToCourseUrl(final String facultyUrl, final String courseIdToken) {
            return facultyUrl.replaceFirst(FACULTY.getTnameQuery(), COURSE.getTnameQuery())
                    + COURSE.makeQueryToken(courseIdToken);
        }

        public static String deepenCourseUrlToSectionUrl(final String courseUrl, final String sectionIdToken) {
            return courseUrl.replaceFirst(COURSE.getTnameQuery(), SECTION.getTnameQuery())
                    + SECTION.makeQueryToken(sectionIdToken);
        }
    }

}
