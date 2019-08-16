package org.bse.data.schedule;

import java.util.Set;

/**
 *
 * @param <S> The type of the [ScheduleIf]'s contents (represent course sections).
 */
interface ScheduleIf<S> {

    Set<S> getCourseSections();

    // implementations should return whether they have any stt sections.
    boolean isBasedOffAnStt();

    String getEnclosedSttName();

    Set<S> getEnclosedSttSections();

}
