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
    private Integer creditValue;
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

    public FacultyTreeNode getFacultyTreeNode() throws IncompleteWipException {
        if (facultyTreeNode != null) {
            return facultyTreeNode;
        } else {
            throw missingProperty("facultyTreeNode");
        }
    }

    public String getCourseIdToken() throws IncompleteWipException {
        if (courseIdToken != null) {
            return courseIdToken;
        } else {
            throw missingProperty("courseIdToken");
        }
    }

    public Integer getCreditValue() throws IncompleteWipException {
        if (creditValue != null) {
            return creditValue;
        } else {
            throw missingProperty("creditValue");
        }
    }

    public String getDescriptionString() throws IncompleteWipException {
        if (descriptionString != null) {
            return descriptionString;
        } else {
            throw missingProperty("descriptionString");
        }
    }

    public Set<CourseLectureSectionWip> getLectureSections() throws IncompleteWipException {
        if (lectureSections != null) {
            return Collections.unmodifiableSet(lectureSections);
        } else {
            throw missingProperty("lectureSections");
        }
    }

    public Set<CourseSectionWip> getLabSections() throws IncompleteWipException {
        if (labSections != null) {
            return Collections.unmodifiableSet(labSections);
        } else {
            throw missingProperty("labSections");
        }
    }

    public Set<CourseSectionWip> getTutorialSections() throws IncompleteWipException {
        if (tutorialSections != null) {
            return Collections.unmodifiableSet(tutorialSections);
        } else {
            throw missingProperty("tutorialSections");
        }
    }

    private IncompleteWipException missingProperty(final String fieldName) {
        return IncompleteWipException.missingProperty(this, fieldName);
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

        public final String getSectionIdToken() throws IncompleteWipException {
            if (sectionIdToken != null) {
                return sectionIdToken;
            } else {
                throw missingProperty("sectionIdToken");
            }
        }

        public final CourseUtils.Semester getSemester() throws IncompleteWipException {
            if (semester != null) {
                return semester;
            } else {
                throw missingProperty("semester");
            }
        }

        public final String getProfessorName() throws IncompleteWipException {
            if (professorName != null) {
                return professorName;
            } else {
                throw missingProperty("professorName");
            }
        }

        public final Set<CourseSectionBlockWip> getBlocks() throws IncompleteWipException {
            if (blocks != null) {
                return Collections.unmodifiableSet(blocks);
            } else {
                throw missingProperty("blocks");
            }
        }

        private IncompleteWipException missingProperty(final String fieldName) {
            return IncompleteWipException.missingProperty(this, fieldName);
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

            public Set<String> getRequiredLabOptionIdTokens() throws IncompleteWipException {
                if (requiredLabOptionIdTokens != null) {
                    return Collections.unmodifiableSet(requiredLabOptionIdTokens);
                } else {
                    throw missingProperty("requiredLabOptionIdTokens");
                }
            }

            public Set<String> getRequiredTutorialOptionIdTokens() throws IncompleteWipException {
                if (requiredTutorialOptionIdTokens != null) {
                    return Collections.unmodifiableSet(requiredTutorialOptionIdTokens);
                } else {
                    throw missingProperty("requiredTutorialOptionIdTokens");
                }
            }

            private IncompleteWipException missingProperty(final String fieldName) {
                return IncompleteWipException.missingProperty(this, fieldName);
            }
        }

        /**
         *
         */
        public static final class CourseSectionBlockWip implements WorkInProgress {

            private Boolean isWaitlist;
            private CourseUtils.WeekDay weekDay;
            private CourseSectionBlock.BlockRepetition repetitionType;
            private UbcTimeUtils.BlockTime beginTime;
            private UbcTimeUtils.BlockTime endTime;
            // TODO [repr][CourseSectionBlockWip]: add representation for location (building) when available.

            public CourseSectionBlockWip setWaitlist(Boolean waitlist) {
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

            public Boolean isWaitlist() throws IncompleteWipException {
                if (isWaitlist != null) {
                    return isWaitlist;
                } else {
                    throw missingProperty("isWaitlist");
                }
            }

            public CourseUtils.WeekDay getWeekDay() throws IncompleteWipException {
                if (weekDay != null) {
                    return weekDay;
                } else {
                    throw missingProperty("weekDay");
                }
            }

            public CourseSectionBlock.BlockRepetition getRepetitionType() throws IncompleteWipException {
                if (repetitionType != null) {
                    return repetitionType;
                } else {
                    throw missingProperty("repetitionType");
                }
            }

            public UbcTimeUtils.BlockTime getBeginTime() throws IncompleteWipException {
                if (beginTime != null) {
                    return beginTime;
                } else {
                    throw missingProperty("beginTime");
                }
            }

            public UbcTimeUtils.BlockTime getEndTime() throws IncompleteWipException {
                if (endTime != null) {
                    return endTime;
                } else {
                    throw missingProperty("endTime");
                }
            }

            private IncompleteWipException missingProperty(final String fieldName) {
                return IncompleteWipException.missingProperty(this, fieldName);
            }
        }
    }

}
