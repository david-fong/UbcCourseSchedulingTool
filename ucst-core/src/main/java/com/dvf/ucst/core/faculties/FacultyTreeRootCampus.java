package com.dvf.ucst.core.faculties;

import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.faculties.vancouver.VancouverFaculties;
import com.dvf.ucst.utils.xml.XmlUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * At the top level, everything is first partitioned by campus. All faculties under
 * a common campus must have unique abbreviations.
 *
 * We will go by the hierarchy specified in the second link.
 * - https://www.ubc.ca/our-campuses/vancouver/directories/faculties-schools.html
 * - https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea&tname=subj-all-departments
 */
public interface FacultyTreeRootCampus extends FacultyTreeNode, XmlUtils.XmlConstant {

    Map<String, Course> EMPTY_COURSE_CODE_MAP = Collections.emptyMap();

    @Override
    default String getNameWithTitle() {
        // Here, the title is a suffix instead of a prefix:
        return getNameNoTitle() + getFacultyType().title;
    }

    @Override
    default String getXmlConstantValue() {
        // the method used to decode in [UbcCampuses::getCampusByIdToken] must match this.
        return getAbbreviation();
    }

    @Override
    default FacultyTreeNodeType getFacultyType() {
        return FacultyTreeNodeType.CAMPUS;
    }

    @Override
    default FacultyTreeRootCampus getRootCampus() {
        return this;
    }

    @Override
    default int getDepth() {
        return 0;
    }

    @Override
    default FacultyTreeNode getParentNode() {
        return null;
    }

    @Override
    default Path getRootAnchoredPathToInfo(SubDirectories subDir) {
        return Paths.get(getAbbreviation().toLowerCase()).resolve(subDir.getSubDirectory());
    }

    @Override
    default String getRegistrationSiteUrl() {
        return RegistrationSubjAreaQuery.getCampusUrl(this);
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
                        FacultyTreeNode.class.getName(), childAbbr,
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
        public Map<String, FacultyTreeNode> getSquashedFacultyAbbrMap() {
            return squashedFacultyAbbrMap;
        }

        /**
         * @param campusIdSearchToken Ex. "VAN" or "OKA".
         * @return The [UbcCampuses] instance by the token ID [campusIdSearchToken] if it exists.
         * @throws CampusNotFoundException If no [UbcCampuses] by the token ID [campusIdSearchToken] exists.
         */
        public static UbcCampuses getCampusByIdToken(final String campusIdSearchToken) throws CampusNotFoundException {
            for (final UbcCampuses campus : UbcCampuses.values()) {
                // [::getAbbreviation] is used here to match with [::getXmlConstantValue]
                if (campus.getAbbreviation().equals(campusIdSearchToken)) {
                    return campus;
                }
            }
            throw new CampusNotFoundException(campusIdSearchToken);
        }
    }

}
