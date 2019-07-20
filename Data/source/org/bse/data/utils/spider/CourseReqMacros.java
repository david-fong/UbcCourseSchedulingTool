package org.bse.data.utils.spider;

import org.bse.core.registration.course.Course;
import org.bse.requirement.operators.logicalmatching.LogicAndMatchThreshReq;
import org.bse.requirement.operators.logicalmatching.LogicOrMatchThreshReq;
import org.bse.requirement.operators.matching.CountMatchThreshReq;
import org.bse.requirement.operators.matching.CreditMatchThreshReq;
import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Set;

/**
 * For use in [Course]/[CourseFactory] files.
 */
public final class CourseReqMacros {

    public static LogicAndMatchThreshReq<Course> AND(MatchThreshReqIf<Course>... children) {
        return new LogicAndMatchThreshReq<>(Set.of(children));
    }

    public static LogicOrMatchThreshReq<Course> OR(MatchThreshReqIf<Course>... children) {
        return new LogicOrMatchThreshReq<>(Set.of(children));
    }

    public static CountMatchThreshReq<Course> COUNT_REQ(int countThreshold, Course... candidates) {
        return new CountMatchThreshReq<>(countThreshold, Set.of(candidates));
    }

    public static CreditMatchThreshReq<Course> CREDIT_REQ(int creditThreshold, Course... candidates) {
        return new CreditMatchThreshReq<>(creditThreshold, Set.of(candidates));
    }

}
