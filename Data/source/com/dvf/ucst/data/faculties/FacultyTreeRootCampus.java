package com.dvf.ucst.data.faculties;

import com.dvf.ucst.data.HyperlinkBookIf;
import com.dvf.ucst.data.courseutils.Course;
import com.dvf.ucst.data.faculties.vancouver.VancouverFaculties;
import com.dvf.ucst.utils.xml.XmlUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * At the top level, everything is first partitioned by campus. All faculties under
 * a common campus must have unique abbreviations.
 */
public interface FacultyTreeRootCampus extends FacultyTreeNode, XmlUtils.XmlConstant {

    Map<String, Course> EMPTY_COURSE_CODE_MAP = Map.of();

    @Override
    default String getNameWithTitle() {
        // Here the title is a suffix instead of a prefix:
        return getNameNoTitle() + getType().title;
    }

    String getCampusIdToken();

    @Override
    default String getXmlConstantValue() {
        return getNameNoTitle();
    }

    @Override
    default FacultyTreeNodeType getType() {
        return FacultyTreeNodeType.CAMPUS;
    }

    @Override
    default FacultyTreeRootCampus getRootCampus() {
        return this;
    }

    @Override
    default FacultyTreeNode getParentNode() {
        return null;
    }

    @Override
    default Path getRootAnchoredPathToInfo(SubDirectories subDir) {
        return Paths.get(getAbbreviation().toLowerCase()).resolve(subDir.subDirName);
    }

    @Override
    default String getRegistrationSiteUrl() {
        return RegistrationSubjAreaQuery.getUrl(this);
    }

    /**
     * @return An unmodifiable [Map] from faculty names under [this] campus to the
     *     corresponding [FacultyTreeNode]s.
     */
    Map<String, FacultyTreeNode> getSquashedFacultyAbbrMap();

    /**
     * @return An empty [Map] because a campus does not have courses directly under it.
     */
    @Override
    default Map<String, Course> getCourseIdTokenToCourseMap() {
        return EMPTY_COURSE_CODE_MAP;
    }



    /**
     *
     */
    enum UbcCampuses implements FacultyTreeRootCampus {
        VANCOUVER ("Vancouver", "VAN", VancouverFaculties.class, "UBC"),
        //OKANAGAN  ("Okanagan", "OKA", OkanaganFaculties.class, "UBCO"),
        ;
        private final String name;
        private final String campusIdToken;
        private final Class<? extends FacultyTreeNode> childrenClass;
        private final String urlQueryTokenVal;
        private final Map<String, FacultyTreeNode> squashedFacultyAbbrMap; // unmodifiable

        <T extends Enum & FacultyTreeNode> UbcCampuses(String name, String campusIdToken,
                                                       Class<T> childrenClass, String urlQueryTokenVal) {
            this.name = name;
            this.campusIdToken = campusIdToken;
            this.childrenClass = childrenClass;
            this.urlQueryTokenVal = urlQueryTokenVal;

            final Map<String, FacultyTreeNode> squashedFacultyAbbrMap = new HashMap<>();
            recursiveInitSquashedFacultyAbbrMap(squashedFacultyAbbrMap, this);
            this.squashedFacultyAbbrMap = Collections.unmodifiableMap(squashedFacultyAbbrMap);
        }

        private void recursiveInitSquashedFacultyAbbrMap(final Map<String, FacultyTreeNode> abbrMap, final FacultyTreeNode scrub) {
            if (scrub != null) {
                for (FacultyTreeNode childNode : scrub.getChildren()) {
                    final String childAbbr = childNode.getAbbreviation();
                    assert !abbrMap.containsKey(childAbbr) : String.format(
                            "Found two [%s]s going by the abbreviation \"%s\" - one"
                            + " under the node \"%s\", and the other under the node \"%s\"."
                            + " Requires fix before project deployment; please investigate.",
                            FacultyTreeNode.class.getName(), childAbbr,
                            abbrMap.get(childAbbr).getAbbreviation(),
                            childNode.getAbbreviation()
                    );
                    abbrMap.put(childAbbr, childNode);
                    recursiveInitSquashedFacultyAbbrMap(abbrMap, childNode);
                }
            }
        }

        @Override
        public String getNameNoTitle() {
            return name;
        }

        @Override
        public String getAbbreviation() {
            return urlQueryTokenVal;
        }

        @Override
        public String getCampusIdToken() {
            return campusIdToken;
        }

        @Override
        public FacultyTreeNode[] getChildren() {
            return childrenClass.getEnumConstants();
        }

        @Override
        public Map<String, FacultyTreeNode> getSquashedFacultyAbbrMap() {
            return squashedFacultyAbbrMap;
        }

        /**
         * @param campusIdSearchToken Ex "VAN" or "OKA".
         * @return The [UbcCampuses] instance by the token ID [campusIdSearchToken] if it exists.
         * @throws CampusNotFoundException If no [UbcCampuses] by the token ID [campusIdSearchToken] exists.
         */
        public static UbcCampuses getCampusByIdToken(String campusIdSearchToken) throws CampusNotFoundException {
            for (UbcCampuses campus : UbcCampuses.values()) {
                if (campus.campusIdToken.equals(campusIdSearchToken)) {
                    return campus;
                }
            }
            throw new CampusNotFoundException(campusIdSearchToken);
        }
    }

}
