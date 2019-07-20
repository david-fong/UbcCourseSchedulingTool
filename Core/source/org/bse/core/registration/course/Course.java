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
 * Pretty much an interface that can only be used by extension.
 * To enforce the singleton pattern, final implementations should configure
 * themselves as private static inner-classes of a public [CourseFactory] class.
 * Their constructors should be made private, and they should be created lazily.
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


    /**
     * This pattern allows us to instantiate factories with a common interface
     * (Ie. [getInstance]) for accessing a singleton instance that is lazily
     * instantiated- as opposed to using a static method of identical function
     * in each [Course] implementation, which cannot be used as part of an
     * interface. This should help to avoid unnecessary memory consumption, and
     * also makes the reasoning about the [equals] method extremely simple: Ie.
     * we don't have to override anything.
     */
    public static abstract class CourseFactory {

        // private static Course singleton = null;

        /**
         * @return The singleton instance of the [Course] lazily created by this
         *     [CourseFactory].
         */
        public abstract Course getInstance();

    }

}
