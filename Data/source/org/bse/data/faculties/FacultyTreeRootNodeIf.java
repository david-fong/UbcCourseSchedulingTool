package org.bse.data.faculties;

import org.bse.data.courseutils.Course;
import org.bse.data.faculties.vancouver.VancouverFaculties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * At the top level, everything is first partitioned by campus.
 */
public interface FacultyTreeRootNodeIf extends FacultyTreeNodeIf {

    Map<String, Course> EMPTY_COURSE_CODE_MAP = Map.of();

    @Override
    default FacultyTreeNodeIf getParentNode() {
        return null;
    }

    @Override
    default Path getPathToData() {
        return Path.of(getAbbreviation().toLowerCase());
    }

    @Override
    default Map<String, Course> getCodeStringToCourseMap() {
        return EMPTY_COURSE_CODE_MAP;
    }



    enum UbcCampuses implements FacultyTreeRootNodeIf {
        VANCOUVER (VancouverFaculties.class),
        //OKANAGAN  (null),
        ;
        private final Class<? extends FacultyTreeNodeIf> childrenClass;

        <T extends Enum & FacultyTreeNodeIf> UbcCampuses(Class<T> childrenClass) {
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
            return null;
        }

        @Override
        public FacultyTreeNodeIf[] getChildren() {
            return childrenClass.getEnumConstants();
        }
    }

}
