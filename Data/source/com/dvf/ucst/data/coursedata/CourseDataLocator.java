package com.dvf.ucst.data.coursedata;

import com.dvf.ucst.data.DataMain;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The package of this class specifies that which will be used for generated
 * resources (based off of the generated resources folder) and for deployed
 * local registration data.
 */
public final class CourseDataLocator {

    private static final Path RUNTIME_CAMPUS_DIR;
    private static final Path GENERATED_CAMPUS_DIR;

    static {
        final Path RUNTIME_DATA_MODULE;
        final Path DEVLP_GEN_RESOURCES;
        try {
            RUNTIME_DATA_MODULE = Paths.get(DataMain.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not get runtime full path to module", e);
        }
        DEVLP_GEN_RESOURCES = RUNTIME_DATA_MODULE
                // TODO: make this more robust.
                .getParent() // .../UbcCourseSchedulingTool/out/production/
                .getParent() // .../UbcCourseSchedulingTool/out/
                .getParent() // .../UbcCourseSchedulingTool/
                .resolve(RUNTIME_DATA_MODULE.getFileName().toString())
                .resolve("genresource")
        ;
        final String packagePath = CourseDataLocator.class.getPackageName()
                .replace(".", RUNTIME_DATA_MODULE.getFileSystem().getSeparator());
        RUNTIME_CAMPUS_DIR = RUNTIME_DATA_MODULE.resolve(packagePath);
        GENERATED_CAMPUS_DIR = DEVLP_GEN_RESOURCES.resolve(packagePath);
    }

    public enum StagedDataPath {
        /**
         * This will be used to locate course info saved in generated xml files,
         * so they can be read at runtime and converted into useful objects.
         * The path to a compiled class' package can be obtained using its class'
         * [getPackage] method, and replacing the package separator with [File.
         * separator].
         */
        PRE_DEPLOYMENT (RUNTIME_CAMPUS_DIR),

        /**
         * This will be used when generating xml data representing courses.
         * The path to a class' source code package can be obtained using its
         * class' [getPackage] method, and replacing the package separator with
         * [File.separator]. This only needs to be valid when run from [DataMain]
         */
        POST_DEPLOYMENT (GENERATED_CAMPUS_DIR),

        ;
        public final Path path;

        StagedDataPath(Path path) {
            this.path = path;
        }
    }

}
