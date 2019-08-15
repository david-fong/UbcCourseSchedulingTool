package org.bse.data.schedule;

import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A collection of [Worklist]s with distinct names. Contents can be added,
 * duplicated, removed, and renamed.
 */
public final class WorklistGroup implements XmlUtils.UserDataXml {

    private static final String NAME_OF_COPY_SUFFIX = "~";

    private final Map<String, Worklist> worklists; // values never null.
    private final Set<String> backingNameSet; // unmodifiable.

    public WorklistGroup() {
        this.worklists = new ConcurrentHashMap<>();
        this.backingNameSet = Collections.unmodifiableSet(worklists.keySet());
    }

    public WorklistGroup(final Element worklistGroupElement) throws MalformedXmlDataException {
        this.worklists = new ConcurrentHashMap<>();
        this.backingNameSet = Collections.unmodifiableSet(worklists.keySet());

        for (Element worklistElement : XmlUtils.getChildElementsByTagName(worklistGroupElement, Worklist.Xml.WORKLIST_TAG)) {
            final Worklist worklist = new Worklist(worklistElement);
            if (worklists.containsKey(worklist.getName())) {
                throw new MalformedXmlDataException("Corrupted data: worklists must have unique names");
            } else {
                worklists.put(worklist.getName(), worklist);
            }
        }
    }

    /**
     * @param other A [Schedule] to add a new [Worklist] based on. Must not be null.
     * @param name A name for the new [Worklist]. Operation fails if another
     *     [Worklist] by the same name already exists in this [WorklistGroup].
     *     If [null], will attempt to create a name from [other] if it has one, and
     *     otherwise use a generic placeholder name. Operation will always succeed
     *     if a [null] name is supplied.
     * @return The new [Worklist] based on [other] if the operation was successful,
     *     and [null] otherwise.
     */
    public Worklist addNewBasedOn(Schedule other, String name) {
        final Worklist copy;

        if (other instanceof Worklist) {
            final Worklist otherWorklist = (Worklist)other;
            if (name == null) {
                name = otherWorklist.getName();
                if (otherWorklist.getName().equals(Worklist.Xml.WORKLIST_NAME_ATTR_DEFAULT.getXmlConstantValue())) {
                    name = other.getEnclosedSttName() + NAME_OF_COPY_SUFFIX; // always add when using STT's name.
                }
                name = safeNameCopy(name);
            }
            copy = new Worklist(other, name);
        } else {
            if (name == null) {
                name = safeNameCopy(Worklist.Xml.WORKLIST_NAME_ATTR_DEFAULT.getXmlConstantValue());
            }
            copy = new Worklist(other, name);
        }

        // [copy] if the previous value mapped from key was null,
        // which should only be true if there was no such mapping.
        return worklists.putIfAbsent(name, copy) == null ? null : copy;
    }

    /**
     * @param other Another [WorklistGroup]. This operation will fail if [other]
     *     is not from this [WorklistGroup].
     * @return A copy of [other], which is added to this [WorklistGroup]. The copy's
     *     name will indicate that it is a copy, and is guaranteed not to be the
     *     same as that of any other [Worklist] already in this [WorklistGroup]. If
     *     the operation failed, returns [null] instead.
     */
    public Worklist createCopyOf(Worklist other) {
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
    public Worklist rename(Worklist worklist, String newName) {
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
    public boolean remove(Worklist worklist) {
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
    public Element toXml(final Document document) {
        final Element worklistElement = document.createElement(Xml.WORKLIST_GROUP_TAG.xmlAttrVal);
        for (Worklist worklist : worklists.values()) {
            worklistElement.appendChild(worklist.toXml(document));
        }
        return worklistElement;
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

    private boolean isFromThis(Worklist worklist) {
        return worklists.get(worklist.getName()) == worklist;
    }



    public enum Xml implements XmlUtils.XmlConstant {
        WORKLIST_GROUP_TAG ("WorklistGroup"),
        ;
        private final String xmlAttrVal;

        Xml(String xmlAttrVal) {
            this.xmlAttrVal = xmlAttrVal;
        }

        @Override
        public String getXmlConstantValue() {
            return xmlAttrVal;
        }
    }

}
