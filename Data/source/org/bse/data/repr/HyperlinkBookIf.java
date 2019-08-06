package org.bse.data.repr;

public interface HyperlinkBookIf {

    String REGISTRATION_HOME = "https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea";

    String getRegistrationLink();

    //String getUbcLink();

    //String getDedicatedSiteLink();



    enum QueryTnameVal {
        DEPT ("subj-department"),
        COURSE ("subj-course"),
        ;
        public String value;

        QueryTnameVal(String value) {
            this.value = value;
        }
    }

}
