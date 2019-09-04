package com.dvf.ucst.core.faculties;

import com.dvf.ucst.core.faculties.vancouver.VancouverFaculties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public enum UbcCampuses implements FacultyTreeRootCampus {
    VANCOUVER ("Vancouver", VancouverFaculties.class, "UBC"),
    //OKANAGAN  ("Okanagan", OkanaganFaculties.class, "UBCO"),
    ;
    private final String name;
    private final Class<? extends FacultyTreeNode> childrenClass;
    private final String campusIdToken;
    private final Map<String, FacultyTreeNode> squashedFacultyAbbrMap; // unmodifiable

    <T extends Enum<?> & FacultyTreeNode> UbcCampuses
            (final String name, final Class<T> childrenClass, final String campusIdToken) {
        this.name = name;
        this.childrenClass = childrenClass;
        this.campusIdToken = campusIdToken;

        final Map<String, FacultyTreeNode> squashedFacultyAbbrMap = new HashMap<>();
        recursiveInitSquashedFacultyAbbrMap(squashedFacultyAbbrMap, this);
        this.squashedFacultyAbbrMap = Collections.unmodifiableMap(squashedFacultyAbbrMap);
    }

    private void recursiveInitSquashedFacultyAbbrMap(final Map<String, FacultyTreeNode> abbrMap, final FacultyTreeNode scrub) {
        for (final FacultyTreeNode childNode : scrub.getChildren()) {
//                assert childNode.getParentNode() == scrub : String.format(
//                        "The [%s] \"%s\" does not correctly point back to the parent it is under,"
//                                + " \"%s\", as its parent. Please fix.",
//                        FacultyTreeNode.class.getName(), scrub.getAbbreviation(),
//                        childNode.getAbbreviation()
//                );
            final String childAbbr = childNode.getAbbreviation();
            assert !abbrMap.containsKey(childAbbr) : String.format(
                    "Found two [%s]s going by the abbreviation \"%s\" - one"
                            + " under the node \"%s\", and the other under the node \"%s\"."
                            + " Requires fix before project deployment. Please investigate.",
                    FacultyTreeNode.class, childAbbr,
                    abbrMap.get(childAbbr).getAbbreviation(),
                    childNode.getAbbreviation()
            );
            abbrMap.put(childAbbr, childNode);
            recursiveInitSquashedFacultyAbbrMap(abbrMap, childNode);
        }
    }

    @Override
    public String getNameNoTitle() {
        return name;
    }

    @Override
    public String getAbbreviation() {
        return campusIdToken;
    }

    @Override
    public FacultyTreeNode[] getChildren() {
        return childrenClass.getEnumConstants();
    }

    @Override
    public UbcCampuses getRootCampus() {
        return this;
    }

    @Override
    public String getRegistrationSiteUrl(){
        return RegistrationSubjAreaQuery.getCampusUrl(this);
    }

    @Override
    public Map<String, FacultyTreeNode> getSquashedFacultyAbbrMap() {
        return squashedFacultyAbbrMap;
    }

    /**
     * @param campusIdSearchToken Ex. "VAN" or "OKA". A String that would be
     *     returned from an existing [FacultyTreeRootCampus]' [::getAbbreviation]
     *     method.
     * @return The [UbcCampuses] instance by the token ID [campusIdSearchToken]
     *     if it exists.
     * @throws CampusNotFoundException If no [UbcCampuses] by the token ID
     *     [campusIdSearchToken] exists.
     */
    public static com.dvf.ucst.core.faculties.UbcCampuses getCampusByIdToken(final String campusIdSearchToken) throws CampusNotFoundException {
        for (final com.dvf.ucst.core.faculties.UbcCampuses campus : com.dvf.ucst.core.faculties.UbcCampuses.values()) {
            // [::getAbbreviation] is used here to match with [::getXmlConstantValue]
            if (campus.getAbbreviation().equals(campusIdSearchToken)) {
                return campus;
            }
        }
        throw new CampusNotFoundException(campusIdSearchToken);
    }
}
