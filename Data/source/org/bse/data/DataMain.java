package org.bse.data;

import java.io.File;

/**
 * Runs the spiders to fetch courses data from UBC's registration pages.
 * Overwrites any existing generated source files.
 *
 * Intellij IDEA Run Configuration (Run > Edit Configurations... > [DataMain]):
 * - Virtual Machine options: -enableassertions
 * - Working Directory: $MODULE_WORKING_DIR$
 */
public final class DataMain {

    private static final String PROJECT_NAME = "UbcCourseSchedulingTool";
    private static final String SOURCE_FOLDER_NAME = "source";
    private static final String MODULE_NAME = "Data";
    public static final File MODULE_SOURCES_DIR;
    static {
        final File userDir = new File(System.getProperty("user.dir"));
        assert userDir.getName().equals(DataMain.MODULE_NAME)
                && userDir.getParentFile().getName().equals(PROJECT_NAME)
                : "@Run Configuration: Working Directory should be \"$MODULE_WORKING_DIR$\"";
        MODULE_SOURCES_DIR = new File(userDir, SOURCE_FOLDER_NAME);
        assert MODULE_SOURCES_DIR.isDirectory();
    }

    public static void main(String[] args) {
    }

}
