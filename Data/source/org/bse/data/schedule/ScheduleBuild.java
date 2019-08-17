package org.bse.data.schedule;

import org.bse.data.repr.courseutils.Course.CourseSection;
import org.bse.data.repr.courseutils.CourseSectionNotFoundException;
import org.bse.data.repr.courseutils.CourseSectionRef;
import org.bse.data.repr.faculties.FacultyCourseNotFoundException;
import org.bse.utils.pickybuild.PickyBuild;
import org.bse.utils.xml.MalformedXmlDataException;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Used when generating schedules. While this class is decoupled from the [Schedule]
 * class (ie. does not directly inherit from it), its xml format spec is a superset
 * of the [Schedule] xml format spec.
 */
public class ScheduleBuild implements ScheduleIf<CourseSection>, PickyBuild<CourseSection> {

    final Set<CourseSection> courseSections;
    private final Set<CourseSection> publicSectionsView; // unmodifiable. synced to [courseSections].
    private final String sttName;
    private final Set<CourseSection> sttSections; // unmodifiable.

    // Copy constructor. Used publicly through [copy] method.
    ScheduleBuild(final ScheduleBuild other) {
        this.courseSections = new HashSet<>(other.courseSections);
        this.publicSectionsView = Collections.unmodifiableSet(courseSections);
        this.sttName = other.sttName;
        this.sttSections = other.sttSections;
    }

    // Only here for [Worklist] to call super constructor.
    ScheduleBuild(final Element scheduleBuildElement) throws MalformedXmlDataException {
        this(new Schedule(scheduleBuildElement));
        // No additional fields to parse for this class.
    }

    // used when creating [ScheduleBuild] or [Worklist] based off a [Schedule].
    ScheduleBuild(final Schedule schedule) throws MalformedXmlDataException {
        this.courseSections = new HashSet<>();
        for (CourseSectionRef ref : schedule.getCourseSections()) {
            try {
                courseSections.add(ref.dereference());
            } catch (FacultyCourseNotFoundException | CourseSectionNotFoundException e) {
                throw new MalformedXmlDataException(e);
            }
        }
        this.publicSectionsView = Collections.unmodifiableSet(courseSections);
        this.sttName = schedule.getEnclosedSttName();
        final Set<CourseSection> sttSections = new HashSet<>();
        for (CourseSectionRef ref : schedule.getCourseSections()) {
            try {
                sttSections.add(ref.dereference());
            } catch (FacultyCourseNotFoundException | CourseSectionNotFoundException e) {
                throw new MalformedXmlDataException(e);
            }
        }
        this.sttSections = Collections.unmodifiableSet(sttSections);
    }

    // TODO: add empty constructor for user to create worklist from scratch.

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
    public boolean conflictsWithAny(CourseSection section) {
        return !courseSections.contains(section)
                && courseSections.stream()
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
    public final boolean addIfNoConflicts(CourseSection section) {
        final boolean canAdd = conflictsWithAny(section);
        if (canAdd) {
            courseSections.add(section);
        }
        return canAdd;
    }

//    /**
//     * @return An immutable snapshot of an implementing instance, WITHOUT ANY OF ITS
//     *     UNIQUE BEHAVIOUR. This may be called, for instance, when a student has
//     *     successfully registered into a [Worklist] (a subclass of [ScheduleBuild]),
//     *     and no longer requires any of its mutable behaviour.
//     */
//    public final Schedule createImmutableCopy() {
//        return new Schedule(this);
//    }

    @Override
    public final Set<CourseSection> getCourseSections() {
        return publicSectionsView;
    }

    @Override
    public final boolean isBasedOffAnStt() {
        return !sttSections.isEmpty();
    }

    @Override
    public final String getEnclosedSttName() {
        return sttName;
    }

    @Override
    public final Set<CourseSection> getEnclosedSttSections() {
        return sttSections;
    }

}