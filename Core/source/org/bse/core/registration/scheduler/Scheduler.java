package org.bse.core.registration.scheduler;

/**
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
     *
     * 0.2. [filter]
     * Create a version of the program's co-requisites that excludes all unusable
     * courses. The produced requirement should be valid based on the test in the
     * previous step. (it is possible to produce invalid requirements. Ex. a
     * [CountMatchThresholdReq] with one candidate and a threshold of two). This
     * step has the potential to significantly reduce later workload, especially in
     * the case of elective lists written for multiple specializations like is the
     * case for ECE (unfortunately).
     *
     * 0.3. [filter]
     * For any [MatchThresholdReq] nodes in the narrowed [Requirement] that are
     * considerably large after the previous step (such as elective option lists),
     * ask the user to supply further restrictions based on their own preferences,
     * and then go back to the previous step.
     *
     * 0.4. [filter]
     * Use the narrowed program [Requirement] to generate a collection of all
     * combinations of usable courses that satisfy the narrowed [Requirement] and do
     * not include any more courses than necessary to do so.
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
     * traversal, breaking ties by how many usable sections are available.
     *
     * 1.1. [filter]
     * Translate each combination of (usable) courses produced by [@STEP 0]  into a
     * [Requirement:CourseSection], which is a single [VariadicAndRequirement] where
     * the children requirements are each [CountMatchThreshReq]s with a threshold of
     * one, where the candidates are usable [CourseSection]s with the same [Course]
     * parent, obtained from [@STEP 1.0]. Using these structures is optional since their
     * meaning is so simple. You could use a list(courses) of sets(sections) instead
     * of a [Requirement:CourseSection]. Sort those entries of those lists by the
     * indices of the courses they represent in the list produced by the previous step.
     * /
 *
 * STEP 2.
 * This aim of this step is to produce a collection of all [Schedule]s (collections of
 * [CourseSection]s) that satisfy the user's program/specialization's requirements, and
 * has no time conflicts. This collection will be sorted according to how preferable
 * they are to the user. NOTE: a [Timetable] is a utility class for a [CourseSection] that
 * stores all its blocks in a map from [DayOfWeek]s to collections of non-overlapping
 * [CourseSectionBlock]s.
 * TODO
 */
public class Scheduler {



}
