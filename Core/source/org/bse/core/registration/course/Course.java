package org.bse.core.registration.course;

import org.bse.core.registration.CourseUtils;
import org.bse.core.registration.CourseUtils.CourseSectionType;
import org.bse.core.registration.FacultyTreeNodeIf;
import org.bse.core.registration.scheduler.CourseSchedule;
import org.bse.utils.requirement.operators.matching.CreditValued;
import org.bse.utils.requirement.operators.matching.MatchingRequirementIf;

/**
 * An interface that can only be used by extension (mwahaha).
 *
 * To enforce the lazy singleton pattern, final (non-abstract) implementations
 * should be public with private constructors, and define a public static method
 * called "[getInstance]" that returns a singleton instance of itself, and creates
 * one if it doesn't already exist. The singleton instance should be stored in a
 * private static field.
 */
public abstract class Course implements CreditValued, CodeStringRegistered {

    abstract FacultyTreeNodeIf getFacultyTreeNode();
    abstract CourseSectionType getCourseType();

    abstract String getCourseDescription();
    abstract HyperlinkBookIf getHyperlinkBook();

    /**
     *
     * @return These may return null if they impose no requirements.
     */
    abstract MatchingRequirementIf<CourseSchedule> getPreRequisites();
    abstract MatchingRequirementIf<CourseSchedule> getCoRequisites();

}
