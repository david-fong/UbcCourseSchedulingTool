package org.bse.data.repr.courseutils;

import org.bse.data.repr.Student;
import org.bse.data.repr.faculties.FacultyTreeNodeIf;
import org.bse.data.repr.HyperlinkBookIf;
import org.bse.utils.requirement.Requirement;
import org.bse.utils.requirement.operators.matching.CreditValued;
import org.bse.utils.requirement.operators.matching.MatchingRequirementIf;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An interface that can only be used by extension or by anonymous construction.
 */
public interface Course extends CreditValued, CodeStringPath {

    FacultyTreeNodeIf getFacultyTreeNode();
    String getCourseDescription();
    HyperlinkBookIf getHyperlinkBook();

    /**
     *
     * @return These may return null if they impose no requirements.
     */
    MatchingRequirementIf<CourseSchedule> getPrerequisites();
    MatchingRequirementIf<CourseSchedule> getCorequisites();
    Requirement<Student> getStudentReqs();

    CourseSectionLectureCategory getLecturesDescriptor();
    CourseSectionCategory getLabsDescriptor();
    CourseSectionCategory getTutorialsDescriptor();

    /**
     *
     * @param xmlDocument An XML [Document].
     * @return An anonymous [Course]-classed instance.
     */
    static Course fromXml(Document xmlDocument) {
        final Element courseElement; {
            NodeList nodeList = xmlDocument.getElementsByTagName(Xml.COURSE_TAG.value);
            courseElement = (Element)nodeList.item(0);
        }

        // TODO:
        return new Course() {
            @Override
            public int getCreditValue() {
                return 0;
            }

            @Override
            public String getFullCodeString() {
                return null;
            }

            @Override
            public FacultyTreeNodeIf getFacultyTreeNode() {
                return null;
            }

            @Override
            public String getCourseDescription() {
                return null;
            }

            @Override
            public HyperlinkBookIf getHyperlinkBook() {
                return null;
            }

            @Override
            public MatchingRequirementIf<CourseSchedule> getPrerequisites() {
                return null;
            }

            @Override
            public MatchingRequirementIf<CourseSchedule> getCorequisites() {
                return null;
            }

            @Override
            public Requirement<Student> getStudentReqs() {
                return null;
            }

            @Override
            public CourseSectionLectureCategory getLecturesDescriptor() {
                return null;
            }

            @Override
            public CourseSectionCategory getLabsDescriptor() {
                return null;
            }

            @Override
            public CourseSectionCategory getTutorialsDescriptor() {
                return null;
            }
        };
    }



    enum Xml {
        COURSE_TAG ("Course"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }
    }

}
