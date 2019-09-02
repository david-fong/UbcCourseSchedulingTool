package com.dvf.ucst.core.schedule;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A collection of [Worklist]s with distinct names. Contents can be added,
 * duplicated, removed, and renamed. GUI wrappers for this controller can
 * provide live-feedback to users on [Worklist] name-validity based on the
 * two used criteria:
 * 1) [Worklist] names for [Worklist]s in the same [WorklistGroup] must be
 *    unique (enforced by [WorklistGroup]). See [::getNames]
 * 2) [Worklist] names follow the format specified by [Worklist.PERMITTED_NAME_TESTER].
 *
 */
public final class WorklistGroup implements XmlUtils.UserDataXml {

    private static final String NAME_OF_COPY_SUFFIX = "~";
    private static final String DEFAULT_WORKLIST_NAME = "unnamed";

    private final Map<String, Worklist> worklists; // values are never null.
    private final Set<String> backingNameSet; // unmodifiable. synced to [worklists]' keys.

    // Use when user has no saved worklist group yet.
    public WorklistGroup() {
        this.worklists = new ConcurrentHashMap<>();
        this.backingNameSet = Collections.unmodifiableSet(worklists.keySet());
    }

    public WorklistGroup(final Element worklistGroupElement) throws MalformedXmlDataException {
        this.worklists = new ConcurrentHashMap<>();
        this.backingNameSet = Collections.unmodifiableSet(worklists.keySet());

        for (final Element worklistElement
                : XmlUtils.getChildElementsByTagName(worklistGroupElement, Worklist.Xml.WORKLIST_TAG)
        ) {
            final Worklist worklist = new Worklist(worklistElement);
            if (worklists.containsKey(worklist.getName())) {
                throw new MalformedXmlDataException("Corrupted data:"
                        + "worklists in the same group must have unique names"
                );
            } else {
                worklists.put(worklist.getName(), worklist);
            }
        }
    }

    /**
     * @param name The name to use for the new [Worklist]. May be [null]. If [null],
     *     a generic name will be used and the operation is guaranteed to succeed.
     *     Otherwise, this operation will fail if another [Worklist] in this
     *     [WorklistGroup] already goes by the same name.
     * @return The new [Worklist] that was added to this [WorklistGroup] or [null] if
     *     the operation failed.
     */
    public Worklist addNewFromScratch(final String name) {
        if (worklists.containsKey(name)) {
            return null;
        } else {
            final Worklist newWorklist;
            try {
                newWorklist = new Worklist(name != null ? name : safeNameCopy(DEFAULT_WORKLIST_NAME));
            } catch (Worklist.MalformedWorklistNameArgumentException e) {
                assert name != null : String.format(
                        "The generic name \"%s\" needs to work no matter what."
                                + " Please change either the generic name generator or"
                                + " the name format checker so that this is fixed.",
                        safeNameCopy(DEFAULT_WORKLIST_NAME)
                );
                return null;
            }
            worklists.put(newWorklist.getName(), newWorklist);
            return newWorklist;
        }
    }

    /**
     * @param other A [Schedule] to add a new [Worklist] based on. Must not be [null].
     * @param name A name for the new [Worklist]. Operation fails if another
     *     [Worklist] by the same name already exists in this [WorklistGroup].
     *     If [null], will attempt to create a name from [other] if it has one, and
     *     otherwise use a generic placeholder name. Operation will always succeed
     *     if a [null] name is supplied.
     * @return The new [Worklist] based on [other] if the operation was successful,
     *     and [null] otherwise.
     */
    public Worklist addNewBasedOn(final ScheduleBuild other, final String name) {
        if (name != null && backingNameSet.contains(name)) {
            return null; // fail-fast instead of wasting effort on unused construction.
        }
        final Worklist copy;

        try {
            if (other instanceof Worklist) {
                copy = new Worklist(other, name != null ? name : safeNameCopy(((Worklist)other).getName()));
            } else {
                copy = new Worklist(other, name != null ? name : safeNameCopy(DEFAULT_WORKLIST_NAME));
            }
        } catch (Worklist.MalformedWorklistNameArgumentException e) {
            assert name != null : String.format(
                    "The generic name \"%s\" needs to work no matter what."
                            + " Please change either the generic name generator or"
                            + " the name format checker so that this is fixed.",
                    safeNameCopy(DEFAULT_WORKLIST_NAME)
            );
            return null;
        }

        // at this point, name is guaranteed to work.
        worklists.put(copy.getName(), copy);
        return copy;
    }

    // like above method. name may be null.
    public Worklist addNewBasedOn(final Schedule schedule, final String name) throws MalformedXmlDataException {
        return addNewBasedOn(
                new ScheduleBuild(schedule),
                name != null ? name : safeNameCopy(schedule.getEnclosedSttName())
        );
    }

    /**
     * @param other Another [WorklistGroup]. This operation will fail if [other]
     *     is not from this [WorklistGroup].
     * @return A copy of [other], which is added to this [WorklistGroup]. The copy's
     *     name will indicate that it is a copy, and is guaranteed not to be the
     *     same as that of any other [Worklist] already in this [WorklistGroup]. If
     *     the operation failed, returns [null] instead.
     */
    public Worklist createCopyOf(final Worklist other) {
        if (isFromThis(other)) {
            return addNewBasedOn(other, null);
        } else {
            return null;
        }
    }

    /**
     * @param worklist A [Worklist]. This operation will fail if [worklist] is
     *     not from this [WorklistGroup].
     * @param newName A [String] to set as the new name for [worklist]. This
     *     operation will fail if another [Worklist] in this [WorklistGroup]
     *     already goes by the name [name].
     * @return The renamed [Worklist] if [worklist] is from [this][WorklistGroup]
     *     and [newName] is not already the name of another [Worklist] in this
     *     [WorklistGroup] and null otherwise.
     */
    public Worklist rename(final Worklist worklist, final String newName) {
        if (isFromThis(worklist) && !worklist.isLocked() && !worklists.containsKey(newName)) {
            worklists.remove(worklist.getName(), worklist);
            return addNewBasedOn(worklist, newName);
        } else {
            return null;
        }
    }

    /**
     * @param worklist A [Worklist] to remove. This operation will fail if [worklist]
     *     is not from this [WorklistGroup].
     * @return [true] if [worklist] is in this [WorklistGroup] and was removed.
     */
    public boolean remove(final Worklist worklist) {
        if (isFromThis(worklist) && !worklist.isLocked()) {
            return worklists.remove(worklist.getName(), worklist);
        } else {
            return false;
        }
    }

    /**
     * @return An unmodifiable view of the [Set] of all [Worklist] names in [this]
     *     [WorklistGroup].
     */
    public Set<String> getNames() {
        return backingNameSet;
    }

    @Override
    public Element toXml(final Function<XmlUtils.XmlConstant, Element> elementSupplier) {
        final Element worklistElement = elementSupplier.apply(Xml.WORKLIST_GROUP_TAG);
        for (final Worklist worklist : worklists.values()) {
            worklistElement.appendChild(worklist.toXml(elementSupplier));
        }
        return worklistElement;
    }

    /**
     * @param name A string to get a safe version of to use as a name.
     * @return A [String] to be used as a [Worklist] [name] that has as many of
     *     the [NAME_OF_COPY_SUFFIX] appended to it as are needed so there are
     *     no conflicts with [Worklist]s already in this [WorklistGroup].
     */
    private String safeNameCopy(final String name) {
        String safeName = name;
        while (worklists.containsKey(safeName)) {
            safeName += NAME_OF_COPY_SUFFIX;
        }
        return safeName;
    }

    private boolean isFromThis(final Worklist worklist) {
        return worklists.get(worklist.getName()) == worklist;
    }



    public enum Xml implements XmlUtils.XmlConstant {
        WORKLIST_GROUP_TAG ("WorklistGroup"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
