package org.bse.data;

import org.bse.utils.requirement.InsatiableReqException;
import org.bse.utils.requirement.RequireOpResult;
import org.bse.utils.requirement.operators.matching.CreditMatchThreshReq;
import org.bse.utils.requirement.operators.matching.CreditValued;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

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

    public static class CreditValuedImpl implements CreditValued {

        private final String str;
        private final int creditValue;

        public CreditValuedImpl(String str, int creditValue) {
            this.str = str;
            this.creditValue = creditValue;
        }

        public String getStr() {
            return str;
        }

        @Override
        public int getCreditValue() {
            return creditValue;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof CreditValuedImpl) {
                return str.equals(((CreditValuedImpl)other).getStr());
            } else {
                return false;
            }
        }
    }


    public static void main(String[] args) {
        //System.out.println(RUNTIME_PATH_OF_COMPILED_DATA_MODULE);

        CreditValuedImpl c0 = new CreditValuedImpl("a", 2);
        CreditValuedImpl c1 = new CreditValuedImpl("b", 3);
        CreditValuedImpl c2 = new CreditValuedImpl("c", 4);

        try {
            CreditMatchThreshReq<CreditValuedImpl> creditMatcher0
                    = new CreditMatchThreshReq<>(5, Set.of(c0, c1, c2));

            Set<CreditValuedImpl> testSubject0 = Set.of(c0, c1);

            RequireOpResult.ReqOpOutcome status = creditMatcher0.requireOf(testSubject0);
            System.out.println((status == RequireOpResult.ReqOpOutcome.PASSED_REQ)
                    ? "we passed like we expected!"
                    : "we didn't pass when we expected to >:0"
            );
        } catch (InsatiableReqException e) {
            e.printStackTrace();
        }
    }

}
