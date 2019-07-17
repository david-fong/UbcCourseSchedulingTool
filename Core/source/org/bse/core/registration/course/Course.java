package org.bse.core.registration.course;

import org.bse.core.registration.scheduler.CourseSchedule;
import org.bse.core.registration.CourseUtils;
import org.bse.core.registration.CreditValued;
import org.bse.core.registration.FacultyTreeNodeIf;
import org.bse.core.utils.HyperlinkBookIf;
import org.bse.requirement.Requirement;

import java.util.Set;

/**
 *
 * Pretty much an interface but can only be used by extension.
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
