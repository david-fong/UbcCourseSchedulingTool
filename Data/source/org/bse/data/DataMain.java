package org.bse.data;

import org.bse.utils.requirement.InsatiableReqException;
import org.bse.utils.requirement.RequireOpResult;
import org.bse.utils.requirement.operators.matching.CreditMatchThreshReq;
import org.bse.utils.requirement.operators.matching.CreditValued;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
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
     */
    public static final Path RUNTIME_PATH_OF_COMPILED_DATA_MODULE;

    /**
     * This will be used when generating xml data representing courses.
     * The path to a class' source code package can be obtained using its
     * class' [getPackage] method, and replacing the package separator with
     * [File.separator]. This only needs to be valid when run from [DataMain]
     */
    public static final Path DEVELOPMENT_PATH_TO_GENERATED_RESOURCES;

    static {
        try {
            RUNTIME_PATH_OF_COMPILED_DATA_MODULE = Path.of(DataMain.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            DEVELOPMENT_PATH_TO_GENERATED_RESOURCES = RUNTIME_PATH_OF_COMPILED_DATA_MODULE
                    // TODO: make this more robust.
                    .getParent() // .../UbcCourseSchedulingTool/out/production/
                    .getParent() // .../UbcCourseSchedulingTool/out/
                    .getParent() // .../UbcCourseSchedulingTool/
                    .resolve("genresource")
                    .resolve(RUNTIME_PATH_OF_COMPILED_DATA_MODULE.getFileName().toString())
            ;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not get runtime path to module jar", e);
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
        if (!Files.isDirectory(DEVELOPMENT_PATH_TO_GENERATED_RESOURCES)) {
            throw new RuntimeException("could not get development path to generated resources");
        }
        System.out.println(RUNTIME_PATH_OF_COMPILED_DATA_MODULE);
        System.out.println(DEVELOPMENT_PATH_TO_GENERATED_RESOURCES);

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
