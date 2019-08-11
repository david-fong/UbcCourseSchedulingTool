package org.bse.data.schedule;

import org.bse.data.repr.courseutils.Course.CourseSection;

import java.util.Set;

/**
 * A mutable wrapper for a [Schedule] object. While this class
 * can be used for generating schedules in the [PickyBuildGenerator], it
 * is not intended for such use, which should be left to [ScheduleBuild].
 */
public class Worklist extends ScheduleBuild {

    private final String name;
    private boolean isLocked = false;
    private WorklistFavorability favorability = WorklistFavorability.NEUTRAL;

    Worklist(Schedule otherSchedule, String name) {
        super(otherSchedule);
        this.name = name;

        if (otherSchedule instanceof Worklist) {
            final Worklist otherWorklist = (Worklist)otherSchedule;
            this.isLocked = otherWorklist.isLocked;
            this.favorability = otherWorklist.favorability;
        }
    }

    @Override
    public Worklist copy() {
        return new Worklist(this, name);
    }

    @Override
    public final boolean conflictsWith(CourseSection section) {
        // disables adding [CourseSection]s if locked:
        return isLocked || super.conflictsWith(section);
    }

    /**
     * @param section A [CourseSection] to attempt to remove from this [Worklist].
     * @return [true] if the operation was successful and [false] otherwise.
     */
    public boolean removeSection(CourseSection section) {
        return !isLocked && courseSections.remove(section);
    }

    public final String getName() {
        return name;
    }

    public final boolean isLocked() {
        return isLocked;
    }

    public final WorklistFavorability getFavorability() {
        return favorability;
    }

    public final void setLocked(final boolean locked) {
        this.isLocked = locked;
    }

    public final void setFavorability(WorklistFavorability favorability) {
        this.favorability = favorability;
    }



    /**
     * [CourseSection]s contained in [SttWorklist]s as part its STT cannot be removed.
     * This class is for user interaction with a worklist based on an STT. For a rep
     * without the need for interactive behaviour, use an [ImmutableSchedule].
     *
     * Implementation note: superclasses must only know about the STT sections through
     * the [getCourseSections] method. This means STT section records don't need to be
     * defensively copied as long as they are guaranteed not to be modified anywhere.
     * The backing collection of [CourseSection]s still needs to be defensively copied,
     * though.
     */
    public static final class SttWorklist extends Worklist {

        // Only used for checking operation validity.
        // STT contents must be backed in superclass rep.
        private final Set<CourseSection> sttSections;

        // from an STT:
        SttWorklist(SttSchedule sttSchedule, String name) {
            super(sttSchedule, name);
            this.sttSections = sttSchedule.getCourseSections();
        }

        // from a build generated with an STT as a template:
        SttWorklist(SttScheduleBuild sttScheduleBuild, String name) {
            super(sttScheduleBuild, name);
            this.sttSections = sttScheduleBuild.getSttSections();
        }

        // copy constructor (sections added by the user are retained):
        SttWorklist(SttWorklist otherStt) {
            super(otherStt, otherStt.getName());
            this.sttSections = otherStt.sttSections;
        }

        @Override
        public final boolean isBasedOffAnStt() {
            return true;
        }

        @Override
        public SttWorklist copy() {
            return new SttWorklist(this);
        }

        @Override
        public boolean removeSection(CourseSection section) {
            return !sttSections.contains(section) && super.removeSection(section);
        }
    }



    public enum WorklistFavorability {
        FAVORABLE,
        NEUTRAL,
        UNFAVORABLE,
        ;
    }

}
