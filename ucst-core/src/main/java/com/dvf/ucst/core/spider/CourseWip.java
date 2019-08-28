package com.dvf.ucst.core.spider;

import com.dvf.ucst.core.spider.CourseWip.CourseSectionWip.CourseLectureSectionWip;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.utils.general.WorkInProgress;

import java.util.Set;

/**
 *
 */
public final class CourseWip implements WorkInProgress {

    private FacultyTreeNode facultyTreeNode;
    private String courseIdToken;
    private int creditValue;
    private String descriptionString;

    // TODO: add fields for requirements when those api's are ready.

    private Set<CourseLectureSectionWip> lectureSections;
    private Set<CourseSectionWip> labSections;
    private Set<CourseSectionWip> tutorialSections;

    CourseWip setFacultyTreeNode(FacultyTreeNode facultyTreeNode) {
        this.facultyTreeNode = facultyTreeNode;
        return this;
    }

    CourseWip setCourseIdToken(String courseIdToken) {
        this.courseIdToken = courseIdToken;
        return this;
    }

    CourseWip setCreditValue(int creditValue) {
        this.creditValue = creditValue;
        return this;
    }

    CourseWip setDescriptionString(String descriptionString) {
        this.descriptionString = descriptionString;
        return this;
    }

    CourseWip setLectureSections(Set<CourseLectureSectionWip> lectureSections) {
        this.lectureSections = lectureSections;
        return this;
    }

    CourseWip setLabSections(Set<CourseSectionWip> labSections) {
        this.labSections = labSections;
        return this;
    }

    CourseWip setTutorialSections(Set<CourseSectionWip> tutorialSections) {
        this.tutorialSections = tutorialSections;
        return this;
    }

    public FacultyTreeNode getFacultyTreeNode() {
        return facultyTreeNode;
    }

    public String getCourseIdToken() {
        return courseIdToken;
    }

    public int getCreditValue() {
        return creditValue;
    }

    public String getDescriptionString() {
        return descriptionString;
    }

    public Set<CourseLectureSectionWip> getLectureSections() {
        return lectureSections;
    }

    public Set<CourseSectionWip> getLabSections() {
        return labSections;
    }

    public Set<CourseSectionWip> getTutorialSections() {
        return tutorialSections;
    }


    /**
     *
     */
    public static class CourseSectionWip implements WorkInProgress {

        // private non-final fields:



        /**
         *
         */
        public static final class CourseLectureSectionWip extends CourseSectionWip {

            // private non-final fields:

        }

        /**
         *
         */
        public static final class CourseSectionBLockWip implements WorkInProgress {

            // private non-final fields:

        }
    }

}
