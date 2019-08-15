package org.bse.data.schedule;

import org.bse.data.repr.courseutils.CourseSectionRef;
import org.bse.utils.pickybuild.PickyBuild;
import org.bse.utils.xml.MalformedXmlDataException;
import org.w3c.dom.Element;

/**
 * Used when generating schedules.
 */
public class ScheduleBuild extends Schedule implements PickyBuild<CourseSectionRef> {

    // Copy constructor. Used publicly through [copy] method.
    ScheduleBuild(Schedule other) {
        super(other);
    }

    // Only here for [Worklist] to call super constructor.
    ScheduleBuild(Element scheduleBuildElement) throws MalformedXmlDataException {
        super(scheduleBuildElement);
        // No additional fields to parse for this class.
    }

    @Override
    public ScheduleBuild copy() {
        return new ScheduleBuild(this);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [Schedule].
     *
     * @param section A [CourseSection] to test adding to this [Schedule].
     * @return [true] if [section] can be added without conflicts.
     */
    @Override
    public boolean conflictsWithAny(CourseSectionRef section) {
        return !getCourseSections().contains(section)
                && getCourseSections().stream()
                .noneMatch(section::overlapsWith);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [Schedule].
     *
     * @param section A [CourseSection] to attempt to add to this [Schedule].
     * @return [true] if the operation was successful.
     */
    @Override
    public final boolean addIfNoConflicts(CourseSectionRef section) {
        final boolean canAdd = conflictsWithAny(section);
        if (canAdd) {
            courseSections.add(section);
        }
        return canAdd;
    }

}