package org.bse.core.registration.course;

import org.bse.core.registration.scheduler.CourseSchedule;
import org.bse.core.registration.CourseUtils;
import org.bse.requirement.operators.matching.CreditValued;
import org.bse.core.registration.FacultyTreeNodeIf;
import org.bse.core.utils.HyperlinkBookIf;
import org.bse.requirement.Requirement;

import java.util.Set;

/**
 * In interface that can only be used by extension.
 *
 * To enforce the lazy singleton pattern, final (non-abstract) implementations
 * should be public with private constructors, and define a public static method
 * called "[getInstance]" that returns a singleton instance of itself, and creates
 * one if it doesn't already exist. The singleton instance should be stored in a
 * private static field.
 */
public abstract class Course implements CreditValued, CodeStringRegistered {

    abstract FacultyTreeNodeIf getFacultyTreeNode();
    abstract CourseUtils.CourseType getCourseType();

    abstract String getCourseDescription();
    abstract HyperlinkBookIf getHyperlinkBook();

    /**
     *
     * @return These may return null if they impose no requirements.
     */
    abstract Requirement<Set<CourseSchedule>> getPreRequisites();
    abstract Requirement<CourseSchedule> getCoRequisites();

}
