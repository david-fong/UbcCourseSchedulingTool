package org.bse.core.registration.course;

public interface HyperlinkBookIf {

    String REGISTRATION_HOME = "https://courses.students.ubc.ca/cs/courseschedule";
    String YOU_UBC_HOME      = "https://you.ubc.ca/ubc_programs/";

    // Can be null?
    String getRegistrationLink();

    String getUbcLink();

    String getCampusSpecificLink();

    String getDedicatedSiteLink();

}
