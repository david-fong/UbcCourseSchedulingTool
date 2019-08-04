package org.bse.data.repr.faculties;

import org.bse.data.repr.courseutils.Course;
import org.bse.data.repr.faculties.vancouver.VancouverFaculties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * At the top level, everything is first partitioned by campus.
 */
public interface FacultyTreeRootCampus extends FacultyTreeNode {

    Map<String, Course> EMPTY_COURSE_CODE_MAP = Map.of();

    @Override
    default FacultyTreeRootCampus getRootCampus() {
        return this;
    }

    @Override
    default FacultyTreeNode getParentNode() {
        return null;
    }

    @Override
    default Path getPathToData() {
        return Paths.get(getAbbreviation().toLowerCase());
    }

    /**
     *
     * @return An empty map because a campus does not have courses directly under it.
     */
    @Override
    default Map<String, Course> getCodeStringToCourseMap() {
        return EMPTY_COURSE_CODE_MAP;
    }



    enum UbcCampuses implements FacultyTreeRootCampus {
        VANCOUVER (VancouverFaculties.class),
        //OKANAGAN  (null),
        ;
        private final Class<? extends FacultyTreeNode> childrenClass;

        <T extends Enum & FacultyTreeNode> UbcCampuses(Class<T> childrenClass) {
            this.childrenClass = childrenClass;
            if (!Files.isDirectory(getRuntimeFullPathToData())) {
                throw new RuntimeException("Campus folder does not exist at runtime");
            }
        }

        @Override
        public String getNameNoTitle() {
            return null;
        }

        @Override
        public String getAbbreviation() {
            return null;
        }

        @Override
        public FacultyTreeNodeType getType() {
            return FacultyTreeNodeType.CAMPUS;
        }

        @Override
        public FacultyTreeNode[] getChildren() {
            return childrenClass.getEnumConstants();
        }
    }

}
