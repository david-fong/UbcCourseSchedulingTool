package com.dvf.ucst.core.courseutils.categorymatchers;

/**
 *
 */
class CategoryLanguage {

    /*
    @ naomi. delete this when you don't need it anymore.
    A [Predicate] is a function that takes an object and returns a boolean value.
    in java, the "::" operator is used to refer to a function/method as an object.
    so I could have written this as:

    static Predicate<String> CHECK_OBJECT = new Predicate<String>() {
        return false;
    }

    or also in lambda-style shorthand as:

    static Predicate<String> CHECK_OBJECT = (courseCode) -> {
        return false;
    }

    and in either of these cases, in CourseCategory.java, it would instead say

    LANGUAGE (CategoryLanguage.CHECK_OBJECT)

     */
    static boolean CHECK(final String courseCode) {
        return false; // TODO:
    }

}
