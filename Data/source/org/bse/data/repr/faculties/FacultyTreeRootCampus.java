package org.bse.data.repr.faculties;

import org.bse.data.repr.HyperlinkBookIf;
import org.bse.data.repr.courseutils.Course;
import org.bse.data.repr.faculties.vancouver.VancouverFaculties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * At the top level, everything is first partitioned by campus.
 */
public interface FacultyTreeRootCampus extends FacultyTreeNode {

    Map<String, Course> EMPTY_COURSE_CODE_MAP = Map.of();

    @Override
    default String getNameWithTitle() {
        // Here the title is a suffix instead of a prefix:
        return getNameNoTitle() + getType().title;
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
     *
     * @return An empty [Map] because a campus does not have courses directly under it.
     */
    @Override
    default Map<String, Course> getCodeStringToCourseMap() {
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
        private final String urlQueryTokenVal;

        <T extends Enum & FacultyTreeNode> UbcCampuses(String name, Class<T> childrenClass, String urlQueryTokenVal) {
            this.name = name;
            this.childrenClass = childrenClass;
            this.urlQueryTokenVal = urlQueryTokenVal;
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
    }

}
