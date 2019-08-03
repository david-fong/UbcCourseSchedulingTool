package org.bse.data.coursedata;

import org.bse.data.DataMain;

import java.nio.file.Path;

/**
 *
 */
public final class CourseDataLocator {

    public static final Path RUNTIME_CAMPUS_JUMP_DIR_PATH
            = DataMain.RUNTIME_PATH_OF_COMPILED_DATA_MODULE
            .resolve(CourseDataLocator.class.getPackageName()
            .replace(".", DataMain.RUNTIME_PATH_OF_COMPILED_DATA_MODULE
                    .getFileSystem().getSeparator()
            ));

    public static final Path DEVELOPMENT_PATH_TO_GENERATED_CAMPUS_FACULTY_DATA
            = DataMain.DEVELOPMENT_PATH_TO_GENERATED_RESOURCES
            .resolve(CourseDataLocator.class.getPackageName()
            .replace(".", DataMain.DEVELOPMENT_PATH_TO_GENERATED_RESOURCES
                    .getFileSystem().getSeparator()
            ));

}
