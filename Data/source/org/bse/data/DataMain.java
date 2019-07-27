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

    public static final File RUNTIME_PATH_OF_DATA_MODULE;
    public static final String SOURCE_FOLDER_NAME = "source";
    static {
        File runtimePathOfDataModule = null;
        try {
            runtimePathOfDataModule = new File(DataMain.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1); // Goodbye.
        }
        RUNTIME_PATH_OF_DATA_MODULE = runtimePathOfDataModule;
    }


    public static void main(String[] args) {
        System.out.println(RUNTIME_PATH_OF_DATA_MODULE);
    }

}
