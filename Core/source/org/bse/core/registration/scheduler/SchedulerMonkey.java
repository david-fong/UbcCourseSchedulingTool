package org.bse.core.registration.scheduler;

import org.bse.core.registration.course.Course;
import org.bse.core.registration.course.CourseSection;
import org.bse.requirement.Requirement;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * STEP 0. [producer]
 * This step aims to take a program/specialization and generate a collection of all
 * usable combinations of courses based ONLY ON PREREQUISITES and not on other info
 * such as section times or available seating. Its aim is to filter out unusable
 * electives from further processing.
     * *
     * 0.0. [control]
     * Taking the user's program/specialization, check if they meet the requirements
     * for being in that program (?)
     *
     * 0.1. [control]
     * Do a preliminary check to discontinue further operations if it is immediately
     * known that it is impossible for the user to satisfy the program's required
     * course options based on their prerequisites.
     * >>> See [Requirement.requireOf]
     *
     * 0.2. [filter]
     * Create a version of the program's co-requisites that excludes all unusable
     * courses. The produced requirement should be valid based on the test in the
     * previous step. (it is possible to produce invalid requirements. Ex. a
     * [CountMatchThresholdReq] with one candidate and a threshold of two). This
     * step has the potential to significantly reduce later workload, especially in
     * the case of elective lists written for multiple specializations like is the
     * case for ECE (unfortunately).
     * >>> See [Requirement.excludingPassingTermsFor]
     *
     * 0.3. [filter]
     * For any [MatchThresholdReq] nodes in the narrowed [Requirement] that are
     * considerably large after the previous step (such as elective option lists),
     * ask the user to supply further restrictions based on their own preferences,
     * and then go back to the previous step.
     * >>> See [MatchThreshReqIf.getNumBarelyPassingCombinations]
     *
     * 0.4. [filter]
     * Use the narrowed program [Requirement] to generate a collection of all
     * combinations of usable courses that satisfy the narrowed [Requirement] and do
     * not include any more courses than necessary to do so.
     * >>> See [MatchThreshReqIf.getAllBarelyPassingCombinations]
     * /
 *
 * STEP 1.
 * This step aims to create a collection of [Requirement:CourseSection] representing
 * all [Course] combinations that could fulfill the user's program/specialization's
 * course requirements, and the usable sections for those courses. This collection is
 * sorted by the overall availability of usable sections for courses in each entry
 * with most the most restrictive entries first.
     * *
     * 1.0. [filter]
     * Take the narrowed [Requirement] from [@STEP 0] and traverse it, collecting its
     * items ([Course]s) to a map from those [Course]s to their available sections.
     * Exclude [CourseSection]s that the user cannot register for and report their
     * reasons. Sort entries of this map first by the depth they were found during the
     * traversal, breaking ties by how many usable sections are available. The reasoning
     * behind this is that depth closely relates to how 'optional' those courses are,
     * and that the number of sections translates to how non-restrictive the collection
     * of usable sections is for a course to the user. This information will be used
     * when generating schedules in this way: the least-optional and most-scheduling-
     * restrictive courses should be the first to attempt to add to a generating schedule.
     * By starting with the least-flexible players ([CourseSections]), we can avoid
     * treading down many unfruitful paths.
     * >>> See [CourseSchedule.canAddSection]
     *
     * 1.1. [filter]
     * Translate each combination of (usable) courses produced by [@STEP 0] into a
     * [MatchThreshReqIf:CourseSection], which is a single [LogicalAndMatchThreshReq]
     * where the children requirements are each [CountMatchThreshReq]s with a threshold
     * of one, where the candidates are usable [CourseSection]s with the same [Course]
     * parent, obtained from [@STEP 1.0]. Using these structures is optional since their
     * meaning is so simple. You could use a collection(courses) of sets(sections) instead
     * of a [MatchThreshReqIf:CourseSection]. (Sort those entries of those lists by the
     * indices of the courses they represent in the list produced by the previous step?)
     * /
 *
 * STEP 2.
 * This aim of this step is to produce a collection of all [Schedule]s (collections of
 * [CourseSection]s) that satisfy the user's program/specialization's requirements, and
 * has no time conflicts. This collection will be sorted according to how preferable
 * they are to the user.
 * TODO
 */

/**
 * If spiders get to be called spiders because they traverse the web,
 * then I get to call this thing a [SchedulerMonkey] because it traverses
 * [Requirement] trees.
 */
public final class SchedulerMonkey {

    // TODO: narrow down typing bounds:
    /**
     * NOTE: "PU" stands for "Possibly Usable" Ie. Using all information prior to
     * initialization, excludes all children that are known to be impossible to use.
     * All fields of this class should be effectively final (not modified or mutated)
     * after they are initialized.
     *
     * TODO: change these from fields to variables in static methods
     */
    private Requirement<?> PUProgramReqs;           // Initialized during 0.2.

    private Collection<Set<Course>> PUCourseCombos; // Initialized during 0.4.

    private List<Map.Entry<Course, Set<CourseSection>>> PUCourseSections; // Initialized during 1.0.

    // Used as a more direct representation of a List<VariadicAndRequirement<Set<CourseSection>>>
    // where the AND requirements children are all CountMatchThreshReq<Set<CourseSection>>> with
    // thresholds of one, and their respective candidates are all usable [CourseSection]s of the
    // same parent [Course].
    private List<Set<Set<CourseSection>>> AllPUPUSectionCombos; // Initialized during 1.1.

    static void STEP_0() {
        return;
    }

}
