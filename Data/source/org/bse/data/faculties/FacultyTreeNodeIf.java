package org.bse.data.faculties;

import org.bse.data.courseutils.Course;
import org.bse.utils.xml.XmlFileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import static org.bse.data.DataMain.RUNTIME_PATH_OF_COMPILED_DATA_MODULE;

/**
 * A property of a [Course].
 */
public interface FacultyTreeNodeIf {

    String getNameNoTitle();

    default String getNameWithTitle() {
        return getType().title + getNameNoTitle();
    }

    String getAbbreviation();

    FacultyTreeNodeType getType();

    /* These must return null if they do not apply.
     * Each node in the returned array of some implementation
     * "A"'s implementation of getChildren must return A.
     */
    FacultyTreeNodeIf getParentNode();
    FacultyTreeNodeIf[] getChildren();

    default FacultyTreeRootNodeIf getRootFacultyNode() {
        return getParentNode() == null
                ? (FacultyTreeRootNodeIf)this
                : getParentNode().getRootFacultyNode()
                ;
    }

    /**
     *
     * @param codeString must not be null. Ex "101"
     * @return The course registered by the code [codeString].
     * @throws FacultyCourseNotFoundException If a file could not be located under
     *     [this][FacultyTreeNodeIf] following the path spec during lazy initialization
     *     of the [Course] registered by the code [codeString].
     */
    default Course getCourseByCodeString(String codeString) throws FacultyCourseNotFoundException {
        if (!getCodeStringToCourseMap().containsKey(codeString)) {
            final String message = "code string not registered in faculty tree node";
            throw new FacultyCourseNotFoundException(message, this);
        }
        Course course = getCodeStringToCourseMap().get(codeString);
        if (course != null) {
            return course;
        } else {
            course = initCourseOfCodeString(codeString);
            getCodeStringToCourseMap().put(codeString, course);
            return course;
        }
    }
    private Course initCourseOfCodeString(String courseCodeStringToken) throws FacultyCourseNotFoundException {
        File filePath = new File(
                RUNTIME_PATH_OF_COMPILED_DATA_MODULE,
                String.join(File.separator,
                        getRootFacultyNode().getCampusFolderName(),
                        getAbbreviation(),
                        courseCodeStringToken
                )
                // It is the [Spider]'s responsibility to create xml files in the
                // IDE's resource folder following the above folder structure.
        );
        try {
            return Course.fromXml(
                    XmlFileUtils.readXmlFromFile(filePath)
            );
        } catch (FileNotFoundException e) {
            throw new FacultyCourseNotFoundException(courseCodeStringToken, this, e);
        }
    }

    /**
     *
     * @return A map from course code strings to [Course]s. Must not be null.
     *     All existing [Course]s under the implementing [FacultyTreeNodeIf]
     *     must have their code string as a key from initialization. Keys in
     *     the returned set must never change once initialized.
     */
    Map<String, Course> getCodeStringToCourseMap();



    /**
     * I've never understood what the deal was with
     * "school", "institute", and "centre". Sheesh.
     */
    enum FacultyTreeNodeType {
        FACULTY    ("Faculty of "),
        SCHOOL     ("School of "),
        // INSTITUTE,
        // CENTRE,
        DEPARTMENT ("Department of "),
        ;
        public final String title;

        FacultyTreeNodeType(String title) {
            this.title = title;
        }
    }

}

