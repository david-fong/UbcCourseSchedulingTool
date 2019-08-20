package com.dvf.ucst.data.schedule;

import com.dvf.ucst.data.courseutils.CourseSectionRef;
import com.dvf.ucst.data.faculties.FacultyCourseNotFoundException;
import com.dvf.ucst.data.courseutils.Course.CourseSection;
import com.dvf.ucst.data.courseutils.CourseSectionNotFoundException;
import com.dvf.ucst.utils.pickybuild.PickyBuild;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Used when generating schedules. While this class is decoupled from the [Schedule]
 * class (ie. does not directly inherit from it), its xml format spec is a superset
 * of the [Schedule] xml format spec.
 *
 * UBC's registration site allows users to remove a whole STT (if one exists) from a
 * worklist. I don't want this in our implementation for two reasons: 1. Because the
 * whole point of creating a worklist with an STT is to work with (and around) that STT.
 * 2. We can go without it because our implementation is stronger: User's can create
 * worklists based off other schedules (including STT's), whereas with UBC's registration
 * site, you can only create empty worklists and you'd have to later ADD an STT to them.
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

    // Used when creating a [ScheduleBuild] or a [Worklist] based off a [Schedule].
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

    // Used as super constructor when users decide to create a [Worklist] from scratch.
    ScheduleBuild() {
        this.courseSections = new HashSet<>();
        this.publicSectionsView = Collections.unmodifiableSet(courseSections);
        this.sttName = Schedule.STT_NAME_FOR_SCHEDULE_WITHOUT_AN_STT;
        this.sttSections = Collections.emptySet();
    }

    @Override
    public ScheduleBuild copy() {
        return new ScheduleBuild(this);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [Schedule].
     *
     * @param section A [CourseSection] to attempt to add to this [Schedule].
     * @return [true] if the operation was successful.
     */
    @Override
    public boolean addIfNoConflicts(final CourseSection section) {
        if (publicSectionsView.contains(section)) {
            return true;
        } else if (publicSectionsView.stream().anyMatch(section::overlapsWith)) {
            return false;
        } else {
            courseSections.add(section);
            return true;
        }
    }

    @Override
    public boolean containsAny(final Collection<CourseSection> others) {
        return others.stream().anyMatch(publicSectionsView::contains);
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