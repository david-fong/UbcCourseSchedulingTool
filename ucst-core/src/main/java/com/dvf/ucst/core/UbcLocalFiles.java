package com.dvf.ucst.core;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
@FunctionalInterface
public interface UbcLocalFiles {

    /**
     * @return A [Path] to the implementing instance, rooted under the appropriate
     *     [UbcLocalDataCategory].
     */
    Path getLocalDataPath();

    /**
     *
     */
    enum UbcLocalDataCategory {
        COURSES ("courses"),
        PROGRAMS ("programs"),
        STTS ("stts"),
        ;
        private final Path rootDir;

        UbcLocalDataCategory(final String rootDirName) {
            this.rootDir = Paths.get(rootDirName);
        }

        public Path getRootDir() {
            return rootDir;
        }



        public enum CourseSubDirs {
            SUB_FACULTIES ("subfaculties"),
            COURSE_XML ("coursedefs");

            private final String subDirName;

            CourseSubDirs(final String subDirName) {
                this.subDirName = subDirName;
            }

            public String getSubDirName() {
                return subDirName;
            }
        }
    }

}
