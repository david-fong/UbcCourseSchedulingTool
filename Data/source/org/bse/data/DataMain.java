package org.bse.data;

import org.bse.data.coursedata.CourseDataLocator;
import org.bse.utils.requirement.RequireOpResult;
import org.bse.utils.requirement.operators.matching.CreditMatchThreshReq;
import org.bse.utils.requirement.operators.matching.CreditValued;

import java.nio.file.Files;
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
     * nested class annotation
     */
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


    /**
     * main annotation
     * @param args Commandline arguments.
     */
    public static void main(String[] args) {
        if (!Files.isDirectory(CourseDataLocator.GENERATED_CAMPUS_DIR)) {
            throw new RuntimeException("could not get development path to generated resources");
        }
        System.out.println(CourseDataLocator.RUNTIME_CAMPUS_DIR);
        System.out.println(CourseDataLocator.GENERATED_CAMPUS_DIR);

        CreditValuedImpl c0 = new CreditValuedImpl("a", 2);
        CreditValuedImpl c1 = new CreditValuedImpl("b", 3);
        CreditValuedImpl c2 = new CreditValuedImpl("c", 4);

        CreditMatchThreshReq<CreditValuedImpl> creditMatcher0
                = new CreditMatchThreshReq<>(5, Set.of(c0, c1, c2));

        Set<CreditValuedImpl> testSubject0 = Set.of(c0, c1);

        RequireOpResult.ReqOpOutcome status = creditMatcher0.requireOf(testSubject0);
        System.out.println((status == RequireOpResult.ReqOpOutcome.PASSED_REQ)
                ? "we passed like we expected!"
                : "we didn't pass when we expected to >:0"
        );
    }

}
