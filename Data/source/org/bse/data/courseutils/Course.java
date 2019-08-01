package org.bse.data.courseutils;

import org.bse.data.faculties.FacultyTreeNodeIf;
import org.bse.utils.requirement.operators.matching.CreditValued;
import org.bse.utils.requirement.operators.matching.MatchingRequirementIf;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An interface that can only be used by extension (mwahaha).
 *
 * To enforce the lazy singleton pattern, final (non-abstract) implementations
 * should be public with private constructors, and define a public static method
 * called "[getInstance]" that returns a singleton instance of itself, and creates
 * one if it doesn't already exist. The singleton instance should be stored in a
 * private static field.
 */
public abstract class Course implements CreditValued, CodeStringPath {

    abstract FacultyTreeNodeIf getFacultyTreeNode();
    abstract String getCourseDescription();
    abstract HyperlinkBookIf getHyperlinkBook();

    /**
     *
     * @return These may return null if they impose no requirements.
     */
    abstract MatchingRequirementIf<CourseSchedule> getPreRequisites();
    abstract MatchingRequirementIf<CourseSchedule> getCoRequisites();

    /**
     *
     * @param xmlDocument An XML [Document].
     * @return An anonymous [Course]-classed instance.
     */
    public static Course fromXml(Document xmlDocument) {
        final Element courseElement; {
            NodeList nodeList = xmlDocument.getElementsByTagName(Xml.COURSE_TAG.value);
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
            FacultyTreeNodeIf getFacultyTreeNode() {
                return null;
            }

            @Override
            String getCourseDescription() {
                return null;
            }

            @Override
            HyperlinkBookIf getHyperlinkBook() {
                return null;
            }

            @Override
            MatchingRequirementIf<CourseSchedule> getPreRequisites() {
                return null;
            }

            @Override
            MatchingRequirementIf<CourseSchedule> getCoRequisites() {
                return null;
            }
        };
    }



    private enum Xml {
        COURSE_TAG ("Course"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }
    }

}
