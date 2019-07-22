package org.bse.data.utils.spider;

import org.bse.core.registration.course.Course;
import org.bse.requirement.operators.logicalmatching.LogicAndMatchReq;
import org.bse.requirement.operators.logicalmatching.LogicOrMatchReq;
import org.bse.requirement.operators.matching.CountMatchThreshReq;
import org.bse.requirement.operators.matching.CreditMatchThreshReq;
import org.bse.requirement.operators.matching.MatchingRequirementIf;

import java.util.Set;

/**
 * For use in [Course]/[CourseFactory] files.
 */
public final class CourseReqMacros {

    public static LogicAndMatchReq<Course> AND(MatchingRequirementIf<Course>... children) {
        return new LogicAndMatchReq<>(Set.of(children));
    }

    public static LogicOrMatchReq<Course> OR(MatchingRequirementIf<Course>... children) {
        return new LogicOrMatchReq<>(Set.of(children));
    }

    public static CountMatchThreshReq<Course> COUNT_REQ(int countThreshold, Course... candidates) {
        return new CountMatchThreshReq<>(countThreshold, Set.of(candidates));
    }

    public static CreditMatchThreshReq<Course> CREDIT_REQ(int creditThreshold, Course... candidates) {
        return new CreditMatchThreshReq<>(creditThreshold, Set.of(candidates));
    }

}
