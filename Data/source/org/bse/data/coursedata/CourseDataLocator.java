package org.bse.data.coursedata;

import org.bse.data.DataMain;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public final class CourseDataLocator {

    private static final Path RUNTIME_DATA_MODULE;
    private static final Path DEVLP_GEN_RESOURCES;

    /**
     * This will be used to locate course info saved in generated xml files,
     * so they can be read at runtime and converted into useful objects.
     * The path to a compiled class' package can be obtained using its class'
     * [getPackage] method, and replacing the package separator with [File.
     * separator].
     */
    public static final Path RUNTIME_CAMPUS_DIR;

    /**
     * This will be used when generating xml data representing courses.
     * The path to a class' source code package can be obtained using its
     * class' [getPackage] method, and replacing the package separator with
     * [File.separator]. This only needs to be valid when run from [DataMain]
     */
    public static final Path GENERATED_CAMPUS_DIR;

    static {
        try {
            RUNTIME_DATA_MODULE = Paths.get(DataMain.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not get runtime path to module jar", e);
        }
        DEVLP_GEN_RESOURCES = RUNTIME_DATA_MODULE
                // TODO: make this more robust.
                .getParent() // .../UbcCourseSchedulingTool/out/production/
                .getParent() // .../UbcCourseSchedulingTool/out/
                .getParent() // .../UbcCourseSchedulingTool/
                .resolve(RUNTIME_DATA_MODULE.getFileName().toString())
                .resolve("genresource")
        ;
        final String separator = RUNTIME_DATA_MODULE.getFileSystem().getSeparator();
        final String packagePath = CourseDataLocator.class.getPackageName().replace(".", separator);
        RUNTIME_CAMPUS_DIR = RUNTIME_DATA_MODULE.resolve(packagePath);
        GENERATED_CAMPUS_DIR = DEVLP_GEN_RESOURCES.resolve(packagePath);;
    }

}
