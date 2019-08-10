package org.bse.data.repr;

import org.bse.data.repr.courseutils.Course;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlParsingUtils;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TODO [doc]:
 */
public final class WorklistGroup {

    private static final String NAME_OF_COPY_SUFFIX = "~";

    private final ConcurrentMap<String, Worklist> worklists;

    public WorklistGroup() {
        this.worklists = new ConcurrentHashMap<>();
    }

    /**
     * @param name A string to get a safe version of to use as a name.
     * @return A [String] to be used as a [Worklist] [name] that has as many of
     *     the [NAME_OF_COPY_SUFFIX] appended to it as are needed so there are
     *     no conflicts with [Worklist]s already in this [WorklistGroup].
     */
    private String safeNameCopy(String name) {
        String safeName = name;
        while (worklists.containsKey(safeName)) {
            safeName += NAME_OF_COPY_SUFFIX;
        }
        return safeName;
    }

    /**
     * @param other A [CourseSchedule] to add a new [Worklist] based on.
     * @param name A name for the new [Worklist]. Operation fails if another
     *     [Worklist] by the same name is already part of this [WorklistGroup].
     * @return [true] if the operation was successful.
     */
    public boolean addNewBasedOn(CourseSchedule other, String name) {
        return worklists.putIfAbsent(name, new Worklist(other, name)) == null;
    }

    /**
     * @param other Another [WorklistGroup]. This operation will fail if [other]
     *     is not from this [WorklistGroup].
     * @return A copy of [other], which is added to this [WorklistGroup]. The copy's
     *     name will indicate that it is a copy, and is guaranteed not to be the
     *     same as that of any other [Worklist] already in this [WorklistGroup].
     */
    public boolean createCopyOf(Worklist other) {
        if (isFromThis(other)) {
            return addNewBasedOn(other, safeNameCopy(other.name));
        } else {
            return false;
        }
    }

    /**
     * @param worklist A [Worklist]. This operation will fail if [worklist] is
     *     not from this [WorklistGroup].
     * @param newName A [String] to set as the new name for [worklist].
     * @return [true] if [worklist] is from [this] [WorklistGroup] and [newName]
     *     is not already the name of another [Worklist] in this [WorklistGroup].
     */
    public boolean rename(Worklist worklist, String newName) {
        if (isFromThis(worklist) && !worklist.isLocked) {
            worklists.remove(worklist.name);
            return addNewBasedOn(worklist, newName);
        } else {
            return false;
        }
    }

    /**
     * @param worklist A [Worklist] to remove. This operation will fail if [worklist]
     *     is not from this [WorklistGroup].
     * @return [true] if [worklist] is in this [WorklistGroup] and was removed.
     */
    public boolean remove(Worklist worklist) {
        if (isFromThis(worklist) && !worklist.isLocked) {
            return worklists.remove(worklist.name) != null;
        } else {
            return false;
        }
    }

    private boolean isFromThis(Worklist worklist) {
        return worklist.getContext() == this;
        // return worklists.get(worklist.name) == worklist;
    }



    /**
     * A mutable wrapper for a [CourseSchedule] object. [name] is immutable.
     * Implementation is final with no public constructors. While this class
     * can be used for generating schedules in the [PickyBuildGenerator], it
     * is not intended for such use, which should be left to [CourseScheduleBuild].
     */
    public class Worklist extends CourseScheduleBuild {

        private final String name;
        private boolean isLocked = false;
        private WorklistFavorability favorability = WorklistFavorability.NEUTRAL;

        private Worklist(String name) {
            this.name = name;
        }

        private Worklist(CourseSchedule otherSchedule, String name) {
            super(otherSchedule);
            this.name = name;

            if (otherSchedule instanceof Worklist) {
                final Worklist otherWorklist = (Worklist)otherSchedule;
                this.isLocked = otherWorklist.isLocked;
            }
        }

        private WorklistGroup getContext() {
            return WorklistGroup.this;
        }

        @Override
        public Worklist copy() {
            Worklist copy = new Worklist(this, safeNameCopy(name));
            worklists.put(copy.name, copy);
            return copy;
        }

        @Override
        public boolean conflictsWith(Course.CourseSection section) {
            // disables adding [CourseSection]s if locked:
            return isLocked || super.conflictsWith(section);
        }

        @Override
        protected boolean removeSection(Course.CourseSection section) {
            return !isLocked && super.removeSection(section);
        }

        public final String getName() {
            return name;
        }

        public final boolean isLocked() {
            return isLocked;
        }

        public final void setLocked(final boolean locked) {
            this.isLocked = locked;
        }

        public final WorklistFavorability getFavorability() {
            return favorability;
        }

        public final void setFavorability(WorklistFavorability favorability) {
            this.favorability = favorability;
        }
    }



    /**
     * [CourseSection]s contained in [SttWorklist]s as part its STT cannot be removed.
     * This class is for user interaction with a worklist based on an STT. For a rep
     * without the need for interactive behaviour, use an [ImmutableCourseSchedule].
     *
     * Implementation note: superclasses must only know about the STT sections through
     * the [getCourseSections] method. This means STT section records don't need to be
     * defensively copied as long as they are guaranteed not to be modified anywhere.
     */
    public final class SttWorklist extends Worklist {

        // Only used for checking operation validity.
        // STT contents must be backed in superclass rep.
        private final Set<Course.CourseSection> sttSections;
        private final Supplier<CourseScheduleBuild> unmodifiedSttWorklistSupplier;

        private SttWorklist(Element sttElement) throws MalformedXmlDataException {
            super(XmlParsingUtils.getMandatoryAttr(sttElement, SttXml.NAME_ATTR).getValue());

            final List<String> sectionRefs = XmlParsingUtils
                    .getElementsByTagName(sttElement, SttXml.SECTION_REF_TAG)
                    .stream().map(Object::toString) // TODO: think of what to do here.
                    .collect(Collectors.toList());
            this.sttSections = Collections.unmodifiableSet(new HashSet<>());
            // TODO: ^implement. also, should entry type be changed to a new class for string-based references?

            this.unmodifiedSttWorklistSupplier = () -> new SttWorklist(sttSections, getName());

            for (Course.CourseSection sttSection : sttSections) {
                addIfNoConflicts(sttSection);
            }
        }

        // copy constructor (sections added by the user are retained):
        private SttWorklist(SttWorklist otherStt) {
            super(otherStt, otherStt.getName());
            this.sttSections = otherStt.sttSections;
            this.unmodifiedSttWorklistSupplier = () -> new SttWorklist(sttSections, getName());
        }

        // bare STT copy constructor (sections added by the user are NOT retained):
        private SttWorklist(Set<Course.CourseSection> sttSections, String name) {
            super(safeNameCopy(name));
            this.sttSections = sttSections;
            this.unmodifiedSttWorklistSupplier = () -> new SttWorklist(sttSections, getName());
        }

        @Override
        public SttWorklist copy() {
            // Use the copy constructor that retains sections added by the user:
            return new SttWorklist(this);
        }

        @Override
        protected boolean removeSection(Course.CourseSection section) {
            return !sttSections.contains(section) && super.removeSection(section);
        }
    }



    public enum WorklistFavorability {
        FAVORABLE,
        NEUTRAL,
        UNFAVORABLE,
        ;
    }

    public enum SttXml implements XmlParsingUtils.XmlConstant {
        STT_TAG ("Stt"),
        NAME_ATTR ("name"),
        SECTION_REF_TAG ("SectionRef"),
        ;
        private final String value;

        SttXml(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

}
