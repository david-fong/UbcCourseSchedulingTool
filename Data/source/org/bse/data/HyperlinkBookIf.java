package org.bse.data;

public interface HyperlinkBookIf {

    String REGISTRATION_HOME = "https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea";

    String getRegistrationSiteUrl();

    //String getUbcLink();

    //String getDedicatedSiteLink();



    enum QuerySpecifierTokens {
        CAMPUS ("campuscd", "subj-all-departments"),
        FACULTY ("dept", "subj-department"),
        COURSE ("course", "subj-course"),
        SECTION ("section", "subj-section")
        ;
        public final String name;
        public final String tokenStub;
        public final String tnameQueryVal;

        public static final String TNAME_QUERY_TOKEN_STUB = "&tname=";

        QuerySpecifierTokens(String name, String tnameQueryVal) {
            this.name = name;
            this.tokenStub = String.format("&%s=", name);
            this.tnameQueryVal = tnameQueryVal;
        }
    }

}
