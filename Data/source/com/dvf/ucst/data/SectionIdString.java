package com.dvf.ucst.data;

public interface SectionIdString {

    // includes campus ID token.
    String getSystemFullSectionIdString();

    // excludes campus ID token. User's don't care about that.
    String getUserFullSectionIdString();

}
