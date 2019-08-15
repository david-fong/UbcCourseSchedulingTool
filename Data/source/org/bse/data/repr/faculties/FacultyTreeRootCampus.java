package org.bse.data.repr.faculties;

import org.bse.data.repr.HyperlinkBookIf;
import org.bse.data.repr.courseutils.Course;
import org.bse.data.repr.faculties.vancouver.VancouverFaculties;
import org.bse.utils.xml.XmlUtils;

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
        return HyperlinkBookIf.REGISTRATION_HOME
                + QuerySpecifierTokens.TNAME_QUERY_TOKEN_STUB
                + QuerySpecifierTokens.CAMPUS.tnameQueryVal
                + QuerySpecifierTokens.CAMPUS.tokenStub
                + getAbbreviation();
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
        private final String courseSectionRefToken;
        private final Class<? extends FacultyTreeNode> childrenClass;
        private final String urlQueryTokenVal;
        private final Map<String, FacultyTreeNode> squashedFacultyAbbrMap; // unmodifiable

        <T extends Enum & FacultyTreeNode> UbcCampuses(String name, String courseSectionRefToken,
                                                       Class<T> childrenClass, String urlQueryTokenVal) {
            this.name = name;
            this.courseSectionRefToken = courseSectionRefToken;
            this.childrenClass = childrenClass;
            this.urlQueryTokenVal = urlQueryTokenVal;

            final Map<String, FacultyTreeNode> squashedFacultyAbbrMap = new HashMap<>();
            recursiveInitSquashedFacultyAbbrMap(squashedFacultyAbbrMap, this);
            this.squashedFacultyAbbrMap = Collections.unmodifiableMap(squashedFacultyAbbrMap);
        }

        private void recursiveInitSquashedFacultyAbbrMap(final Map<String, FacultyTreeNode> map, final FacultyTreeNode scrub) {
            if (scrub != null) {
                for (FacultyTreeNode facultyTreeNode : scrub.getChildren()) {
                    map.putIfAbsent(facultyTreeNode.getAbbreviation(), facultyTreeNode);
                    recursiveInitSquashedFacultyAbbrMap(map, facultyTreeNode);
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
        public FacultyTreeNode[] getChildren() {
            return childrenClass.getEnumConstants();
        }

        @Override
        public Map<String, FacultyTreeNode> getSquashedFacultyAbbrMap() {
            return squashedFacultyAbbrMap;
        }

        /**
         * @param sectionRefToken
         * @return [null] if not found.
         */
        public static UbcCampuses getCampusBySectionRefToken(String sectionRefToken) {
            for (UbcCampuses campus : UbcCampuses.values()) {
                if (campus.courseSectionRefToken.equals(sectionRefToken)) {
                    return campus;
                }
            }
            return null;
        }

    }

}
