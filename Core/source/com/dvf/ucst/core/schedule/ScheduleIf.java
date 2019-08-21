package com.dvf.ucst.core.schedule;

import java.util.Set;

/**
 * The special thing about a schedule being based of an STT is that some sections
 * are only available through an STT. Therefore, the enclosed STT sections must
 * be unmodifiable.
 *
 * @param <S> The type of the [ScheduleIf]'s contents (represent course sections).
 */
interface ScheduleIf<S> {

    Set<S> getCourseSections();

    /**
     * @return Whether this [CourseSection] is based off of an STT (Ie. a subset of
     *     its [CourseSection]s cannot be removed. The value returned by this method
     *     must never vary when called from the same instance multiple times.
     */
    boolean isBasedOffAnStt();

    String getEnclosedSttName();

    Set<S> getEnclosedSttSections();

}
