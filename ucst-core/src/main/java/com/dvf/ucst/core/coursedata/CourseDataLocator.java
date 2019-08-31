package com.dvf.ucst.core.coursedata;

import com.dvf.ucst.core.CoreMain;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The package of this class specifies that which will be used for generated
 * resources (based off of the generated resources folder) and for deployed
 * local registration data.
 *
 * TODO: this is broken after changing project to use gradle conventions. please fix.
 */
public final class CourseDataLocator {

    private static final Path RUNTIME_CAMPUS_DIR;
    private static final Path GENERATED_CAMPUS_DIR;

    static {
        final Path RUNTIME_DATA_MODULE;
        final Path DEVLP_GEN_RESOURCES;
        try {
            RUNTIME_DATA_MODULE = Paths.get(CoreMain.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not get runtime full path to module", e);
        }
        DEVLP_GEN_RESOURCES = RUNTIME_DATA_MODULE
                // TODO: make this more robust.
                             // .../ucst/ucst-core/build/classes/java/main/
                .getParent() // .../ucst/ucst-core/build/classes/java/
                .getParent() // .../ucst/ucst-core/build/classes/
                .getParent() // .../ucst/ucst-core/build/
                .resolve("resources")
                .resolve(RUNTIME_DATA_MODULE.getFileName())
        ;
        final String packagePath = CourseDataLocator.class.getPackageName()
                .replace(".", RUNTIME_DATA_MODULE.getFileSystem().getSeparator());
        RUNTIME_CAMPUS_DIR = RUNTIME_DATA_MODULE.resolve(packagePath);
        GENERATED_CAMPUS_DIR = DEVLP_GEN_RESOURCES.resolve(packagePath);
        assert Files.isDirectory(RUNTIME_CAMPUS_DIR);
        assert Files.isDirectory(GENERATED_CAMPUS_DIR);
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
         * [File.separator]. This only needs to be valid when run from [CoreMain]
         */
        POST_DEPLOYMENT (GENERATED_CAMPUS_DIR),

        ;
        public final Path path;

        StagedDataPath(Path path) {
            this.path = path;
        }
    }

}
