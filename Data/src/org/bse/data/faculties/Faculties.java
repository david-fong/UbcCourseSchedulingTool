package org.bse.data.faculties;

import org.bse.core.utils.FacultyTreeNodeIf;

/**
 * https://www.ubc.ca/our-campuses/vancouver/directories/faculties-schools.html
 */
public enum Faculties implements FacultyTreeNodeIf {
    APPLIED_SCIENCE     ("Faculty of Applied Science"),
    ARCHITECTURE        ("School of Architecture and Landscape Architecture"),
    ARTS                ("Faculty of Arts"),
    BUSINESS            ("Sauder School of Business"),
    COMMUNITY_PLANNING  ("School of Community and Regional Planning"),
    DENTISTRY           ("Faculty of Dentistry"),
    EDUCATION           ("Faculty of Education"),
    EXTENDED_LEARNING   ("Extended Learning"),
    FORESTRY            ("Faculty of Forestry"),
    // TODO: holy moly... :0
    ;
    private final String name;

    Faculties(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FacultyTreeNodeType getType() {
        return FacultyTreeNodeType.FACULTY;
    }
}
