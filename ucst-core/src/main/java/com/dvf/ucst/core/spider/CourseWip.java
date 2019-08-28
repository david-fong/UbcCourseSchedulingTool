package com.dvf.ucst.core.spider;

import com.dvf.ucst.core.courseutils.CourseSectionBlock;
import com.dvf.ucst.core.courseutils.CourseUtils;
import com.dvf.ucst.core.courseutils.UbcTimeUtils;
import com.dvf.ucst.core.spider.CourseWip.CourseSectionWip.CourseLectureSectionWip;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.utils.general.WorkInProgress;

import java.util.Collections;
import java.util.Set;

/**
 * The bridges between the HTML from UBC's registration pages, and the XML files that
 * we will create to save that information persistently, and locally so the app can
 * run without an internet connection.
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
        return Collections.unmodifiableSet(lectureSections);
    }

    public Set<CourseSectionWip> getLabSections() {
        return Collections.unmodifiableSet(labSections);
    }

    public Set<CourseSectionWip> getTutorialSections() {
        return Collections.unmodifiableSet(tutorialSections);
    }



    /**
     *
     */
    public static class CourseSectionWip implements WorkInProgress {

        private String sectionIdToken;
        private CourseUtils.Semester semester;
        private String professorName; // TODO: should this be split into first and last name?
        private Set<CourseSectionBlockWip> blocks;

        final CourseSectionWip setSectionIdToken(String sectionIdToken) {
            this.sectionIdToken = sectionIdToken;
            return this;
        }

        final CourseSectionWip setSemester(CourseUtils.Semester semester) {
            this.semester = semester;
            return this;
        }

        final CourseSectionWip setProfessorName(String professorName) {
            this.professorName = professorName;
            return this;
        }

        final CourseSectionWip setBlocks(Set<CourseSectionBlockWip> blocks) {
            this.blocks = blocks;
            return this;
        }

        public final String getSectionIdToken() {
            return sectionIdToken;
        }

        public final CourseUtils.Semester getSemester() {
            return semester;
        }

        public final String getProfessorName() {
            return professorName;
        }

        public final Set<CourseSectionBlockWip> getBlocks() {
            return Collections.unmodifiableSet(blocks);
        }


        /**
         *
         */
        public static final class CourseLectureSectionWip extends CourseSectionWip {

            private Set<String> requiredLabOptionIdTokens;
            private Set<String> requiredTutorialOptionIdTokens;

            CourseLectureSectionWip setRequiredLabOptionIdTokens(Set<String> requiredLabOptionIdTokens) {
                this.requiredLabOptionIdTokens = requiredLabOptionIdTokens;
                return this;
            }

            CourseLectureSectionWip setRequiredTutorialSectionOptions(Set<String> requiredTutorialOptionIdTokens) {
                this.requiredTutorialOptionIdTokens = requiredTutorialOptionIdTokens;
                return this;
            }

            public Set<String> getRequiredLabOptionIdTokens() {
                return Collections.unmodifiableSet(requiredLabOptionIdTokens);
            }

            public Set<String> getRequiredTutorialOptionIdTokens() {
                return Collections.unmodifiableSet(requiredTutorialOptionIdTokens);
            }
        }

        /**
         *
         */
        public static final class CourseSectionBlockWip implements WorkInProgress {

            private boolean isWaitlist;
            private CourseUtils.WeekDay weekDay;
            private CourseSectionBlock.BlockRepetition repetitionType;
            private UbcTimeUtils.BlockTime beginTime;
            private UbcTimeUtils.BlockTime endTime;
            // TODO [repr][CourseSectionBlockWip]: add representation for location (building) when available.

            public CourseSectionBlockWip setWaitlist(boolean waitlist) {
                isWaitlist = waitlist;
                return this;
            }

            public CourseSectionBlockWip setWeekDay(CourseUtils.WeekDay weekDay) {
                this.weekDay = weekDay;
                return this;
            }

            public CourseSectionBlockWip setRepetitionType(CourseSectionBlock.BlockRepetition repetitionType) {
                this.repetitionType = repetitionType;
                return this;
            }

            public CourseSectionBlockWip setBeginTime(UbcTimeUtils.BlockTime beginTime) {
                this.beginTime = beginTime;
                return this;
            }

            public CourseSectionBlockWip setEndTime(UbcTimeUtils.BlockTime endTime) {
                this.endTime = endTime;
                return this;
            }

            public boolean isWaitlist() {
                return isWaitlist;
            }

            public CourseUtils.WeekDay getWeekDay() {
                return weekDay;
            }

            public CourseSectionBlock.BlockRepetition getRepetitionType() {
                return repetitionType;
            }

            public UbcTimeUtils.BlockTime getBeginTime() {
                return beginTime;
            }

            public UbcTimeUtils.BlockTime getEndTime() {
                return endTime;
            }
        }
    }

}
