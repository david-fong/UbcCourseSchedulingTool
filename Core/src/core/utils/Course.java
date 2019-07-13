package core.utils;

/**
 * TODO: write documentation.
 */
public abstract class Course implements CreditValued {

    private final FacultyTreeNodeIf facultyTreeNode;
    private final CourseCode courseCode;
    private final CourseType courseType;

    private final String description;
    private final HyperlinkBookImpl hyperlinkBook;
    private final int creditValue;

    private final

    public Course(CourseType courseType, String description, int creditValue) {
        this.courseType  = courseType;
        this.description = description;
        this.creditValue = creditValue;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int getCreditValue() {
        return creditValue;
    }



    public static final class CourseCode {

        private final int year;
        private final int value;
        private final String suffix;

        @Override
        public String toString() {
            return value + suffix;
        }

    }

    /**
     * TODO
     */
    public static final class HyperlinkBookImpl implements HyperlinkBookIf {

        @Override
        public String getRegistrationLink() {
            return null;
        }

        @Override
        public String getUbcLink() {
            return null;
        }

        @Override
        public String getCampusSpecificLink() {
            return null;
        }

        @Override
        public String getDedicatedSiteLink() {
            return null;
        }
        
    }



    public enum CourseType {
        PROGRAM  ("Program"),
        LECTURE  ("Lecture"),
        LAB      ("Lab"),
        TUTORIAL ("Tutorial"),
        ;
        private final String label;

        CourseType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

}
