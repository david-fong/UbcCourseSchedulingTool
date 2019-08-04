package org.bse.data.repr;

public interface HyperlinkBookIf {

    String REGISTRATION_HOME = "https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea";

    // Can be null?
    String getRegistrationLink();

    String getUbcLink();

    String getCampusSpecificLink();

    String getDedicatedSiteLink();

}
