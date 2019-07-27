package org.bse.data;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Runs the spiders to fetch courses data from UBC's registration pages.
 * Overwrites any existing generated source files.
 *
 * Intellij IDEA Run Configuration (Run > Edit Configurations... > [DataMain]):
 * - Virtual Machine options: -enableassertions
 * - Working Directory: $MODULE_WORKING_DIR$
 */
public final class DataMain {

    /**
     * This will be used to locate course info saved in generated xml files,
     * so they can be read at runtime and converted into useful objects.
     * The path to a compiled class' package can be obtained using its class'
     * [getPackage] method, and replacing the package separator with [File.
     * separator].
     * TODO: move this closer to where it will be used (which is not here).
     */
    public static final File RUNTIME_PATH_OF_COMPILED_DATA_MODULE;
    static {
        try {
            RUNTIME_PATH_OF_COMPILED_DATA_MODULE = new File(DataMain.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not get runtime path to module jar", e);
        }
    }

    private static final String GENERATED_RESOURCE_FOLDER_NAME = "genresource";
    /**
     * This will be used when generating xml data representing courses.
     * The path to a class' source code package can be obtained using its
     * class' [getPackage] method, and replacing the package separator with
     * [File.separator].
     */
    public static final File DEVELOPMENT_PATH_TO_GENERATED_RESOURCES;
    static {
        final File userDir = new File(System.getProperty("user.dir"));
        if (userDir.getName().equals(RUNTIME_PATH_OF_COMPILED_DATA_MODULE.getName())) {
            DEVELOPMENT_PATH_TO_GENERATED_RESOURCES = new File(
                    RUNTIME_PATH_OF_COMPILED_DATA_MODULE,
                    GENERATED_RESOURCE_FOLDER_NAME
            );
        } else {
            throw new RuntimeException(String.format(
                    "%s must be run from the local path of the module containing its source code",
                    DataMain.class.getName())
            );
        }
    }


    public static void main(String[] args) {
        System.out.println(RUNTIME_PATH_OF_COMPILED_DATA_MODULE);
    }

}
