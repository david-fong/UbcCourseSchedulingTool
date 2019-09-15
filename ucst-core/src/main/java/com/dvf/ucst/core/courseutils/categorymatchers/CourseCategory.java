package com.dvf.ucst.core.courseutils.categorymatchers;

import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlUtils;
import org.w3c.dom.Attr;

import java.util.function.Predicate;

/**
 * the [isCourseInCategoryCheckerFunction] will be defined in its own file in the
 * [categorymatchers] package.
 */
public enum CourseCategory implements XmlUtils.XmlConstant {
    LANGUAGE (CategoryLanguage::CHECK),
    // more things will go here as we find them.
    ;
    private final Predicate<String> isCourseInCategoryCheckerFunction;

    CourseCategory(final Predicate<String> isCourseInCategoryCheckerFunction) {
        this.isCourseInCategoryCheckerFunction = isCourseInCategoryCheckerFunction;
    }

    public boolean isCourseInThisCategory(final String courseCode) {
        return isCourseInCategoryCheckerFunction.test(courseCode);
    }

    @Override
    public String getXmlConstantValue() {
        /*
        @ naomi, delete this comment after reading.
        this is the enum's name in the source code.
        for example, the above enum "LANGUAGE" will return "LANGUAGE".
        we could define names for each one but there's not much point:
        this string will only be seen in xml files and this works well
        because we'll be sure that no names are the same (would mess up
        decoding the xml) (enums of the same type must have different
        names in the source code).
         */
        return name();
    }

    /**
     *
     * @param attr Must not be [null].
     * @return The [CourseCategory] whose [::getXmlConstantValue] equals [xmlAttrValue.getValue()].
     * @throws MalformedXmlDataException If no matching [CourseCategory] could be found.
     */
    public static CourseCategory decodeXmlAttr(final Attr attr) throws MalformedXmlDataException {
        // there may be ways to make this perform better (using a static map from
        // enum names to themselves). for now this is ok!
        for (final CourseCategory category : CourseCategory.values()) {
            if (category.getXmlConstantValue().equals(attr.getValue())) {
                return category;
            }
        }
        throw MalformedXmlDataException.invalidAttrVal(attr);
    }

}
