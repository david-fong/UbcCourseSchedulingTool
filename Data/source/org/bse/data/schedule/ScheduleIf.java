package org.bse.data.schedule;

import java.util.Set;

/**
 *
 * @param <S> The type of the [ScheduleIf]'s contents (represent course sections).
 */
public interface ScheduleIf<S> {

    Set<S> getCourseSections();

    default boolean isBasedOffAnStt() {
        return false;
    }

    String getEnclosedSttName();

    Set<S> getEnclosedSttSections();



}
